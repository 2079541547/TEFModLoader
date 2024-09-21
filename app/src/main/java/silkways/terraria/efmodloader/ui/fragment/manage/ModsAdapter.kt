package silkways.terraria.efmodloader.ui.fragment.manage

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import org.json.JSONObject
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import java.io.InputStream
import java.util.zip.ZipFile

data class ModInfo(
    val filePath: String,
    val runtime: String,
    val identifier: String,
    val modName: String,
    val author: String,
    val introduce: String,
    val version: String,
    val openSource: Boolean,
    val openSourceUrl: String?,
    val customizePage: Boolean,
    var icon: Bitmap? = null
)

fun loadModsFromDirectory(directoryPath: String, context: Context): List<ModInfo> {
    val directory = File(directoryPath)
    val mods = mutableListOf<ModInfo>()

    if (directory.exists() && directory.isDirectory) {
        for (file in directory.listFiles()) {
            if (file.isFile && file.extension != "json") {
                ZipFile(file).use { zip ->
                    val infoEntry = zip.getEntry("info.json")
                    val iconEntry = zip.getEntry("icon.png")

                    if (infoEntry != null && iconEntry != null) {
                        val infoInputStream: InputStream = zip.getInputStream(infoEntry)
                        val jsonString = infoInputStream.bufferedReader().use { it.readText() }
                        val json = JSONObject(jsonString)
                        val info = ModInfo(
                            filePath = file.absolutePath,
                            runtime = json.getString("Runtime"),
                            identifier = json.getString("Identifier"),
                            modName = json.getString("ModName"),
                            author = json.getString("Author"),
                            introduce = json.getString("Introduce"),
                            version = json.getString("Version"),
                            openSource = json.optBoolean("OpenSource", false),
                            openSourceUrl = json.optString("OpenSourceUrl", null),
                            customizePage = json.getBoolean("CustomizePage")
                        )

                        val iconInputStream: InputStream = zip.getInputStream(iconEntry)
                        info.icon = BitmapFactory.decodeStream(iconInputStream)

                        mods.add(info)
                    }
                }
            }
        }
    }

    return mods
}


class ModsAdapter(private val mods: List<ModInfo>, private val context: Context) :
    RecyclerView.Adapter<ModsAdapter.ModViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manage_efmodres_item, parent, false)
        return ModViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ModViewHolder, position: Int) {
        val mod = mods[position]
        holder.title.text = "${mod.modName} - ${mod.author}"
        holder.subtitle.text = mod.version
        holder.switch.isChecked = JsonConfigModifier.readJsonValue(context, "ToolBoxData/EFModData/info.json", mod.filePath) as Boolean
        holder.icon.setImageBitmap(mod.icon)
        holder.itemView.setOnClickListener {
            if (mod.customizePage) {
                showCustomPage(mod)
            }
        }
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            JsonConfigModifier.modifyJsonConfig(context, "ToolBoxData/EFModData/info.json", mod.filePath, isChecked)
        }
    }

    override fun getItemCount(): Int = mods.size

    inner class ModViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val switch: MaterialSwitch = itemView.findViewById(R.id.Setting_Switch)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showCustomPage(mod: ModInfo) {
        // 解压page文件夹到缓存目录
        val cacheDir = context.cacheDir
        val modCacheDir = File(cacheDir, mod.identifier)
        modCacheDir.mkdirs()

        extractPageFolder(mod.filePath, modCacheDir)

        // 使用FileProvider获取文件URI
        val mainHtmlFile = File(modCacheDir, "main.html")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", mainHtmlFile)

        // 加载main.html
        val webView = WebView(context)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            @SuppressLint("ClickableViewAccessibility")
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

            }
        }
        webView.loadUrl(uri.toString())

        // 显示webView，这里可以使用Dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("${mod.modName} - Custom Page")
        builder.setView(webView)
        builder.setPositiveButton("Close") { dialog, _ ->
            deleteCache(modCacheDir)
            dialog.dismiss()
        }
        builder.show()
    }


    private fun extractPageFolder(zipFilePath: String, destinationDir: File) {
        ZipFile(zipFilePath).use { zip ->
            zip.entries().asSequence().filter { it.name.startsWith("Page/") }.forEach { entry ->
                val destFile = File(destinationDir, entry.name.removePrefix("Page/"))
                if (entry.isDirectory) {
                    destFile.mkdirs()
                } else {
                    destFile.parentFile?.mkdirs()
                    zip.getInputStream(entry).use { input ->
                        destFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    // 调试输出
                    println("Extracted: ${destFile.absolutePath}")
                }
            }
        }
    }

    private fun deleteCache(cacheDir: File) {
        if (cacheDir.exists()) {
            cacheDir.deleteRecursively()
        }
    }
}