package silkways.terraria.efmodloader.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.ManageEfmodresDialogBinding
import silkways.terraria.efmodloader.databinding.ManageEfmodsettingDialogBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import eternal.future.effsystem.fileSystem.EFMC
import silkways.terraria.efmodloader.logic.efmod.ModManager
import silkways.terraria.efmodloader.ui.activity.WebActivity

data class ModInfo(
    val filePath: String,
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

                val Infomap = EFMC.getModInfo(file.absolutePath)
                val ModIcon = EFMC.getModIcon(file.absolutePath)

                val info = ModInfo(
                    filePath = file.absolutePath,
                    identifier = Infomap["identifier"].toString(),
                    modName = Infomap["modName"].toString(),
                    author = Infomap["author"].toString(),
                    introduce = Infomap["introduce"].toString(),
                    version = Infomap["version"].toString(),
                    openSource = Infomap["openSource"] as Boolean,
                    openSourceUrl = Infomap["openSourceUrl"].toString(),
                    customizePage = Infomap["customizePage"] as Boolean
                )

                info.icon = BitmapFactory.decodeResource(context.resources, R.drawable.twotone_help_24)
                if (ModIcon.size != 0) info.icon = BitmapFactory.decodeByteArray(ModIcon, 0, ModIcon.size)

                mods.add(info)
            }
        }
    }

    return mods
}


class ModsAdapter(private val mods: List<ModInfo>, private val context: Context) :
    RecyclerView.Adapter<ModsAdapter.ModViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_setting_switch, parent, false)
        return ModViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ModViewHolder, position: Int) {
        val mod = mods[position]
        holder.title.text = "${mod.modName} - ${mod.author}"
        holder.subtitle.text = mod.version
        holder.switch.isChecked = JsonConfigModifier.readJsonValue(context, "TEFModLoader/EFModData/info.json", mod.filePath) as Boolean
        holder.icon.setImageBitmap(mod.icon)
        holder.itemView.setOnClickListener {
            if (mod.customizePage) {
                showCustomPage(mod)
            } else {
                showInfo(mod)
            }
        }

        holder.itemView.setOnLongClickListener{
            showSettingDialog(mod)
            true
        }

        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            JsonConfigModifier.modifyJsonConfig(context, "TEFModLoader/EFModData/info.json", mod.filePath, isChecked)
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

        // 确保context是一个FragmentActivity
        val activity = context as? FragmentActivity ?: return

        // 获取FragmentManager
        val fragmentManager: FragmentManager = activity.supportFragmentManager

        // 查找NavHostFragment
        val navHostFragment = fragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
            ?: throw IllegalStateException("NavHostFragment not found")

        val navOptions = NavOptions.Builder()
            // 设置导航动画
            .setEnterAnim(R.anim.fragment_anim_enter)
            .setExitAnim(R.anim.fragment_anim_exit)
            .setPopEnterAnim(R.anim.fragment_anim_enter)
            .setPopExitAnim(R.anim.fragment_anim_exit)
            .build()


        // 获取NavController
        val navController: NavController = navHostFragment.navController

        // 解压page文件夹到缓存目录
        val cacheDir = context.cacheDir
        val modCacheDir = File(cacheDir, "EFMOD_WEB")
        modCacheDir.mkdirs()


        EFMC.extractPage(mod.filePath, modCacheDir.absolutePath)

        // 使用FileProvider获取文件URI
        val mainHtmlFile = File(modCacheDir, "main.html")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", mainHtmlFile)

        val intent = Intent(context, WebActivity::class.java)

        // 添加额外的数据，即你的信号
        intent.putExtra("isMod", true)
        intent.putExtra("webUrl", uri.toString())
        intent.putExtra("private", "${context.getExternalFilesDir(null)}/EFMod-Private/${mod.identifier}/")

        // 启动新的Activity
        context.startActivity(intent)

        //navController.navigate(R.id.nanavigation_EFModWeb, bundle, navOptions)
    }


    private fun showInfo(mod: ModInfo) {

        var dialogBinding: ManageEfmodresDialogBinding? = ManageEfmodresDialogBinding.inflate(LayoutInflater.from(context))


        val builder = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(dialogBinding?.root)

        val dialog = builder.create().apply {
            // 设置对话框窗口属性
            window?.let { dialogWindow ->
                setCanceledOnTouchOutside(false)

                dialogBinding?.title?.text = mod.modName

                val messageBuilder = StringBuilder()
                messageBuilder.append("${context.getString(R.string.author_text)} ${mod.author}\n")
                messageBuilder.append("${context.getString(R.string.build_text)} ${mod.version}\n")
                messageBuilder.append("${context.getString(R.string.modIntroduce_text)} ${mod.introduce}\n")

                dialogBinding?.tvModDetails?.text = messageBuilder.toString()

                if (!mod.openSource) dialogBinding?.jumpUrl?.visibility = View.GONE

                setOnDismissListener {
                    dialogBinding = null
                }
            }
        }

        dialogBinding?.jumpUrl?.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mod.openSourceUrl))
            context.startActivity(browserIntent)
            dialog.dismiss()
        }

        dialogBinding?.close?.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }

    private fun ModsAdapter.showSettingDialog(mod: ModInfo) {
        var dialogBinding: ManageEfmodsettingDialogBinding? = ManageEfmodsettingDialogBinding.inflate(LayoutInflater.from(context))


        val builder = MaterialAlertDialogBuilder(context)
            .setCancelable(false)
            .setView(dialogBinding?.root)

        val dialog = builder.create().apply {
            // 设置对话框窗口属性
            window?.let { dialogWindow ->
                setCanceledOnTouchOutside(false)

                dialogBinding?.title?.text = mod.modName

                setOnDismissListener {
                    dialogBinding = null
                }
            }
        }

        dialogBinding?.yes?.setOnClickListener {
            ModManager.remove(context, File(mod.filePath), mod.identifier)
            Toast.makeText(context, context.getString(R.string.removeEFMod), Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }

        dialogBinding?.close?.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }

}


