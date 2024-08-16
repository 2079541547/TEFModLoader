package silkways.terraria.toolbox.ui.fragment.manage.mod

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.ManageEfmodresDialogBinding
import silkways.terraria.toolbox.databinding.ManageEfmodsettingDialogBinding
import silkways.terraria.toolbox.logic.mod.ModJsonManager

class ModAdapter(private val modList: MutableList<ModDetail>, private val context: Context) : RecyclerView.Adapter<ModViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.manage_efmodres_item, parent, false)
        return ModViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ModViewHolder, position: Int) {
        val currentMod = modList[position]
        holder.bind(currentMod, context)

        holder.itemView.setOnClickListener {
            showDetailsDialog(it.context, currentMod)
        }

        holder.itemView.setOnLongClickListener {
            showSettingDialog(it.context, currentMod)
            true
        }

        holder.settingSwitch.isChecked = currentMod.enable
        holder.settingSwitch.setOnCheckedChangeListener { _, isChecked ->
            ///sdcard/Android/data/silkways.terraria.toolbox/files/ToolBoxData/ModData/mod_data.json
            ModJsonManager.updateEnableByAuthorAndModName("${context.getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json", currentMod.author, currentMod.modName, isChecked)
        }
    }

    override fun getItemCount(): Int = modList.size

    private fun showDetailsDialog(context: Context, modDetail: ModDetail) {

        var dialogBinding: ManageEfmodresDialogBinding? = ManageEfmodresDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(context)
            .setCancelable(false)
            .setView(dialogBinding?.root)
        val dialog = builder.create().apply{
            //设置窗口特性
            window?.let { dialogWindow ->
                dialogWindow.setBackgroundDrawable(ColorDrawable(0x000000001)) // 设置背景透明
                setCanceledOnTouchOutside(false) // 设置触摸对话框外部不可取消
            }

            val messageBuilder = StringBuilder()
            messageBuilder.append("${context.getString(R.string.author_text)} ${modDetail.author}\n")
            messageBuilder.append("${context.getString(R.string.build_text)} ${modDetail.build}\n")
            messageBuilder.append("${context.getString(R.string.modIntroduce_text)} ${modDetail.modIntroduce}\n")

            if (modDetail.Opencode) {
                messageBuilder.append("${context.getString(R.string.Opencode_text1)}${context.getString(R.string.Opencode_text2)}\n${context.getString(R.string.Opencode_text3)}${modDetail.OpencodeUrl}\n\n")
            } else {
                messageBuilder.append("${context.getString(R.string.Opencode_text1)}${context.getString(R.string.Opencode_text4)}\n\n")
                dialogBinding?.jumpUrl?.visibility = View.GONE
            }

            messageBuilder.append("${context.getString(R.string.mod_more_info)}\n")

            modDetail.functions.forEachIndexed { index, functionHook ->
                messageBuilder.append("${index + 1}. ${context.getString(R.string.position_text)} ${functionHook.position}\n")
                messageBuilder.append("   ${context.getString(R.string.functions_text)} ${functionHook.functions.joinToString(", ")}\n")
                messageBuilder.append("   ${context.getString(R.string.type_text)} ${functionHook.type}\n")
                messageBuilder.append("   ${context.getString(R.string.arrays_text)} ${functionHook.arrays}\n")
            }

            dialogBinding?.title?.text = modDetail.modName
            dialogBinding?.tvModDetails?.text = messageBuilder.toString()


            // 设置对话框关闭监听器
            setOnDismissListener {
                //毁尸灭迹
                dialogBinding = null
            }
        }

        dialogBinding?.jumpUrl?.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(modDetail.OpencodeUrl))
            context.startActivity(browserIntent)
            dialog.dismiss()
        }

        dialogBinding?.close?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showSettingDialog(context: Context, modDetail: ModDetail) {

        var dialogBinding: ManageEfmodsettingDialogBinding? = ManageEfmodsettingDialogBinding.inflate(LayoutInflater.from(context))

        val builder = AlertDialog.Builder(context)
            .setCancelable(false)
            .setView(dialogBinding?.root)
        val dialog = builder.create().apply{
            //设置窗口特性
            window?.let { dialogWindow ->
                dialogWindow.setBackgroundDrawable(ColorDrawable(0x000000001)) // 设置背景透明
                setCanceledOnTouchOutside(false) // 设置触摸对话框外部不可取消
            }

            val messageBuilder = StringBuilder()


            messageBuilder.append("${context.getString(R.string.mod_more_info)}\n")



            modDetail.functions.forEachIndexed { index, functionHook ->
                messageBuilder.append("${index + 1}. ${context.getString(R.string.position_text)} ${functionHook.position}\n")
                messageBuilder.append("   ${context.getString(R.string.arrays_text)} ${functionHook.arrays}\n")
                messageBuilder.append("   ${context.getString(R.string.functions_text)} ${functionHook.functions.joinToString(", ")}\n")
                messageBuilder.append("   ${context.getString(R.string.type_text)} ${functionHook.type}\n")
            }

            dialogBinding?.title?.text = modDetail.modName
            dialogBinding?.tvModDetails?.text = messageBuilder.toString()


            // 设置对话框关闭监听器
            setOnDismissListener {
                //毁尸灭迹
                dialogBinding = null
            }
        }



        dialogBinding?.yes?.setOnClickListener {

            val inputText = dialogBinding?.TextInputEditText?.text?.toString()
            if (inputText != null) {
                // 分割字符串
                val parts = inputText.split("[, ]".toRegex()).map { it.trim() }.toTypedArray()

                if (parts.size == 2) {
                    val firstInt = Integer.parseInt(parts[0]) - 1
                    val secondInt = Integer.parseInt(parts[1])

                    if (firstInt in modDetail.functions.indices) {
                        val functionHook = modDetail.functions[firstInt]
                        ModJsonManager.modifyArrays("${context.getExternalFilesDir(null)?.absolutePath}/ToolBoxData/ModData/mod_data.json", modDetail.author, modDetail.modName, functionHook.position, secondInt)
                    }
                } else {
                    println("Invalid input format.")
                }
            } else {
                println("TextInputEditText text is null.")
            }





            dialog.dismiss()
        }

        dialogBinding?.close?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}

class ModViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
    val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
    val subtitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)
    val settingSwitch: MaterialSwitch = itemView.findViewById(R.id.Setting_Switch)

    @SuppressLint("SetTextI18n")
    fun bind(modDetail: ModDetail, context: Context) {
        titleTextView.text = modDetail.modName
        subtitleTextView.text = "${modDetail.author} - v${modDetail.build}"
        settingSwitch.isChecked = modDetail.enable

        val iconPath = "${context.getExternalFilesDir(null)?.absolutePath}/ToolBoxData/ModData/${modDetail.modName}-${modDetail.author}/icon.png"
        iconImageView.setImageBitmap(BitmapFactory.decodeFile(iconPath))
    }
}