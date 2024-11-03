package silkways.terraria.efmodloader.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import eternal.future.effsystem.fileSystem
import org.json.JSONObject
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.ManageEfmodresDialogBinding
import silkways.terraria.efmodloader.databinding.ManageEfmodsettingDialogBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.efmod.LoaderManager
import java.io.File
import java.io.FileOutputStream

data class LoaderInfo(
    val filePath: String,
    val LoaderName: String,
    val author: String,
    val introduce: String,
    val version: String,
    val openSourceUrl: String,
    var icon: Bitmap? = null
)

fun loadLoaderFromDirectory(directoryPath: String, context: Context): List<LoaderInfo> {
    val directory = File(directoryPath)
    val mods = mutableListOf<LoaderInfo>()

    if (directory.exists() && directory.isDirectory) {
        for (file in directory.listFiles()) {
            if (file.isFile && file.extension != "json") {

                val Infomap = fileSystem.EFML.getLoaderInfo(file.absolutePath)

                val info = LoaderInfo(
                    filePath = file.absolutePath,
                    LoaderName = Infomap["LoaderName"].toString(),
                    author = Infomap["author"].toString(),
                    introduce = Infomap["introduce"].toString(),
                    version = Infomap["version"].toString(),
                    openSourceUrl = Infomap["openSourceUrl"].toString(),
                )


                info.icon = BitmapFactory.decodeResource(context.resources, R.drawable.twotone_help_24)

                try {
                    val ModIcon = fileSystem.EFML.getLoaderIcon(file.absolutePath)
                    if (ModIcon.size != 0) info.icon = BitmapFactory.decodeByteArray(ModIcon, 0, ModIcon.size)
                } catch (A: Exception) {
                    println(A)
                    info.icon = BitmapFactory.decodeResource(context.resources, R.drawable.twotone_help_24)
                }

                mods.add(info)
            }
        }
    }

    return mods
}


class LoaderAdapter(private val mods: List<LoaderInfo>, private val context: Context) :
    RecyclerView.Adapter<LoaderAdapter.ModViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.manage_efmodres_item, parent, false)
        return ModViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ModViewHolder, position: Int) {
        val Loader = mods[position]
        holder.title.text = "${Loader.LoaderName} - ${Loader.author}"
        holder.subtitle.text = Loader.version
        holder.switch.isChecked = JsonConfigModifier.readJsonValue(context, "TEFModLoader/EFModLoaderData/info.json", Loader.filePath) as Boolean
        holder.icon.setImageBitmap(Loader.icon)
        holder.itemView.setOnClickListener {
            showInfo(Loader)
        }

        holder.itemView.setOnLongClickListener{
            showSettingDialog(Loader)
            true
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->

            isLoader(
                "${context.getExternalFilesDir(null)}/TEFModLoader/EFModLoaderData/info.json",
                Loader.filePath,
                isChecked
            )
        }
    }


    private fun isLoader(filePath: String, keyName: String, newValue: Boolean) {
        // 创建文件对象
        val file = File(filePath)

        // 读取文件内容并转化为字符串
        val jsonString = file.bufferedReader().use { it.readText() }

        // 解析JSON字符串
        val jsonObject = JSONObject(jsonString)

        // 更新特定键的值，并将所有其他的布尔类型的键设置为false
        jsonObject.keys().forEach { key ->
            if (key == keyName) {
                jsonObject.put(key, newValue)
            } else if (jsonObject.optBoolean(key)) {
                jsonObject.put(key, false)
            }
        }

        // 将修改后的JSON对象写回到文件中
        FileOutputStream(file).use { fos ->
            fos.write(jsonObject.toString(4).toByteArray())
        }
    }

    override fun getItemCount(): Int = mods.size

    inner class ModViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val subtitle: TextView = itemView.findViewById(R.id.subtitle)
        val switch: MaterialSwitch = itemView.findViewById(R.id.Setting_Switch)
        val icon: ImageView = itemView.findViewById(R.id.icon)
    }


    private fun showInfo(loader: LoaderInfo) {

        var dialogBinding: ManageEfmodresDialogBinding? = ManageEfmodresDialogBinding.inflate(LayoutInflater.from(context))


        val builder = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(dialogBinding?.root)

        val dialog = builder.create().apply {
            // 设置对话框窗口属性
            window?.let { dialogWindow ->
                setCanceledOnTouchOutside(false)

                dialogBinding?.title?.text = loader.LoaderName

                val messageBuilder = StringBuilder()
                messageBuilder.append("${context.getString(R.string.author_text)} ${loader.author}\n")
                messageBuilder.append("${context.getString(R.string.build_text)} ${loader.version}\n")
                messageBuilder.append("${context.getString(R.string.modIntroduce_text)} ${loader.introduce}\n")

                dialogBinding?.tvModDetails?.text = messageBuilder.toString()

                setOnDismissListener {
                    dialogBinding = null
                }
            }
        }

        dialogBinding?.jumpUrl?.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(loader.openSourceUrl))
            context.startActivity(browserIntent)
            dialog.dismiss()
        }

        dialogBinding?.close?.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun showSettingDialog(mod: LoaderInfo) {
        var dialogBinding: ManageEfmodsettingDialogBinding? = ManageEfmodsettingDialogBinding.inflate(LayoutInflater.from(context))


        val builder = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(dialogBinding?.root)

        val dialog = builder.create().apply {
            // 设置对话框窗口属性
            window?.let { dialogWindow ->
                setCanceledOnTouchOutside(false)

                dialogBinding?.title?.text = mod.LoaderName

                setOnDismissListener {
                    dialogBinding = null
                }
            }
        }

        dialogBinding?.yes?.setOnClickListener {
            LoaderManager.remove(context, File(mod.filePath))
            Toast.makeText(context, context.getString(R.string.removeEFMod), Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialogBinding?.close?.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }

}


