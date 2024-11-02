package silkways.terraria.efmodloader.ui.fragment.home.settings

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.SettingsPacknameDialogBinding
import silkways.terraria.efmodloader.databinding.HomeFragmentSettingsBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier

class SettingsFragment: Fragment() {

    private var _binding: HomeFragmentSettingsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.settings)


        /*
        * @navHostFragment 获取导航控管理器
        * @navOptions 导航动画
        * navHostFragment.navController.navigate(R.id.页面id, null, navOptions)
        * 跳转方法
         */

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_anim_enter)
            .setExitAnim(R.anim.fragment_anim_exit)
            .setPopEnterAnim(R.anim.fragment_anim_enter)
            .setPopExitAnim(R.anim.fragment_anim_exit)
            .build()

        // 设置ActionBar的标题
        val activity = requireActivity() as AppCompatActivity


        _binding = HomeFragmentSettingsBinding.inflate(inflater, container, false)

        data class SettingTitle(val title: String)
        data class SettingButton(val title: String, val subTitle: String, val iconResId: Int, val onClick: (View) -> Unit)
        data class SettingSwitch(val title: String, val subTitle: String, val iconResId: Int, val isChecked: Boolean, val onCheckedChange: (Boolean) -> Unit)
        data class Divider(val isDivider: Boolean = true)



        binding.SettingsRecyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            private val VIEW_TYPE_TITLE = 0
            private val VIEW_TYPE_BUTTON = 1
            private val VIEW_TYPE_SWITCH = 2
            private val VIEW_TYPE_DIVIDER = 3


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return when (viewType) {
                    VIEW_TYPE_TITLE -> TitleViewHolder(inflater.inflate(R.layout.item_title, parent, false))
                    VIEW_TYPE_BUTTON -> ButtonViewHolder(inflater.inflate(R.layout.home_setting_item1, parent, false))
                    VIEW_TYPE_SWITCH -> SwitchViewHolder(inflater.inflate(R.layout.home_setting_item2, parent, false))
                    VIEW_TYPE_DIVIDER -> DividerViewHolder(inflater.inflate(R.layout.item_divider, parent, false))
                    else -> throw IllegalArgumentException("Unknown view type")
                }
            }


            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                when (holder) {
                    is TitleViewHolder -> {
                        val settingTitle = settingsList[position] as SettingTitle
                        holder.bind(settingTitle.title)
                    }
                    is ButtonViewHolder -> {
                        val settingButton = settingsList[position] as SettingButton
                        holder.bind(settingButton.title, settingButton.subTitle, settingButton.iconResId, settingButton.onClick)
                    }
                    is SwitchViewHolder -> {
                        val settingSwitch = settingsList[position] as SettingSwitch
                        holder.bind(settingSwitch.title, settingSwitch.subTitle, settingSwitch.iconResId, settingSwitch.isChecked, settingSwitch.onCheckedChange)
                    }
                    is DividerViewHolder -> {}
                }
            }


            private fun getTheme_text(): String {
                when(JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.themeKey)){
                    0 -> return getString(R.string.settings_theme_1)
                    1 -> return getString(R.string.settings_theme_2)
                    2 -> return getString(R.string.settings_theme_3)
                }
                return "错误"
            }

            private fun getLanguage_Text(): String {
                when(JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.languageKey)){
                    0 -> return getString(R.string.settings_language_1)
                    1 -> return getString(R.string.settings_language_2)
                    2 -> return getString(R.string.settings_language_3)
                    3 -> return getString(R.string.settings_language_4)
                    4 -> return getString(R.string.settings_language_5)
                }
                return "错误"
            }

            private fun getRuntime_Text(): String {
                when(JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.Runtime)){
                    0 -> return getString(R.string.runtime_1)
                    1 -> return getString(R.string.runtime_2)
                }
                return "错误"
            }

            fun restartApp(context: Context) {
                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 清除到这个Activity之上的所有Activity
                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 以新的任务栈项的形式启动
                context.startActivity(intent)
            }

            fun showRestartDialog(){
                val builder = MaterialAlertDialogBuilder(requireActivity())

                builder.setTitle(getString(R.string.RestartDialog_title))

                builder.setPositiveButton(getString(R.string.RestartApp)) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    restartApp(requireActivity())
                }

                builder.setNegativeButton(getString(R.string.close)) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }

                val dialog: Dialog = builder.create()
                dialog.show()
            }


            private val settingsList = listOf(

                //设置标题
                SettingTitle(getString(R.string.settings_1)),


                SettingButton(getString(R.string.runtime), getRuntime_Text(), R.drawable.twotone_memory_24) {
                    val popupMenu = PopupMenu(requireActivity(), it)
                    popupMenu.menuInflater.inflate(R.menu.settings_runtime, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_runtime_0 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.Runtime, 0)
                                true
                            }
                            R.id.menu_runtime_1 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.Runtime, 1)
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                },


                SettingButton(getString(R.string.TargetPackageName), JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.GamePackageName) as String, R.drawable.twotone_workspaces_24) {
                    // 初始化Dialog的绑定对象
                    var dialogBinding: SettingsPacknameDialogBinding? = SettingsPacknameDialogBinding.inflate(LayoutInflater.from(requireActivity()))

                    // 创建对话框构建器
                    val builder = MaterialAlertDialogBuilder(requireActivity())
                        .setCancelable(true)
                        .setView(dialogBinding?.root)

                    val dialog = builder.create().apply {
                        // 设置对话框窗口属性
                        window?.let { dialogWindow ->
                            setCanceledOnTouchOutside(true) // 设置触摸对话框外部可取消
                        }


                        dialogBinding?.button?.setOnClickListener {
                            JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.GamePackageName, dialogBinding?.TextInputEditText?.text.toString())
                            dismiss()
                        }

                        // 设置对话框关闭监听器
                        setOnDismissListener {
                            dialogBinding = null
                        }
                    }

                    dialog.show()
                },


                //主题设置
                SettingButton(getString(R.string.settings_theme), getTheme_text(), R.drawable.twotone_color_lens_24) {

                    val popupMenu = PopupMenu(requireActivity(), it)
                    popupMenu.menuInflater.inflate(R.menu.settings_theme_menu, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_theme_1 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.themeKey, 0)
                                showRestartDialog()
                                true
                            }
                            R.id.menu_theme_2 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.themeKey, 1)
                                showRestartDialog()
                                true
                            }
                            R.id.menu_theme_3 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.themeKey, 2)
                                showRestartDialog()
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                },

                //语言设置
                SettingButton(getString(R.string.settings_language), getLanguage_Text(), R.drawable.twotone_language_24) {
                    val popupMenu = PopupMenu(requireActivity(), it)
                    popupMenu.menuInflater.inflate(R.menu.settings_language_menu, popupMenu.menu)

                    popupMenu.setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.menu_language_1 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.languageKey, 0)
                                showRestartDialog()
                                true
                            }
                            R.id.menu_language_2 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.languageKey, 1)
                                showRestartDialog()
                                true
                            }
                            R.id.menu_language_3 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.languageKey, 2)
                                showRestartDialog()
                                true
                            }
                            R.id.menu_language_4 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.languageKey, 3)
                                showRestartDialog()
                                true
                            }
                            R.id.menu_language_5 -> {
                                JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.languageKey, 4)
                                showRestartDialog()
                                true
                            }
                            else -> false
                        }
                    }
                    popupMenu.show()
                },

                SettingSwitch(getString(R.string.Directly_replace_the_file), getString(R.string.Directly_replace_the_file_subtitle),
                    R.drawable.twotone_auto_awesome_motion_24, JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.CoveringFiles) as Boolean) { isChecked ->
                    JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.CoveringFiles, isChecked)
                },

                SettingSwitch(getString(R.string.Automatically_clear_cache), getString(R.string.Automatically_clear_cache_subTitle),
                    R.drawable.twotone_cleaning_services_24, JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.autoClean) as Boolean) { isChecked ->
                    JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.autoClean, isChecked)
                },

                SettingSwitch(getString(R.string.clear_dialogs), getString(R.string.clear_dialogs_subtitle),
                    R.drawable.twotone_clear_all_24, JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.CleanDialog) as Boolean) { isChecked ->
                    JsonConfigModifier.modifyJsonConfig(requireActivity(), Settings.jsonPath, Settings.CleanDialog, isChecked)
                },

                //分割线
                //Divider()


            )
            override fun getItemCount(): Int {
                return settingsList.size
            }


            override fun getItemViewType(position: Int): Int {
                return when (settingsList[position]) {
                    is SettingTitle -> VIEW_TYPE_TITLE
                    is SettingButton -> VIEW_TYPE_BUTTON
                    is SettingSwitch -> VIEW_TYPE_SWITCH
                    is Divider -> VIEW_TYPE_DIVIDER
                    else -> throw IllegalArgumentException("Unknown item type")
                }
            }


            inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)

                fun bind(title: String) {
                    titleTextView.text = title
                    titleTextView.setTextColor(ContextCompat.getColor(requireActivity(), R.color.md_theme_primary))
                }
            }

            inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
                private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
                private val subTitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)

                fun bind(title: String, subTitle: String, iconResId: Int, onClick: (View) -> Unit) {
                    titleTextView.text = title
                    subTitleTextView.text = subTitle
                    iconImageView.setImageResource(iconResId)
                    itemView.setOnClickListener { it ->
                        onClick(it)
                    }
                }
            }

            inner class SwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                private val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
                private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
                private val subTitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)
                private val switchWidget: MaterialSwitch = itemView.findViewById(R.id.Setting_Switch)

                fun bind(title: String, subTitle: String, iconResId: Int, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
                    titleTextView.text = title
                    subTitleTextView.text = subTitle
                    iconImageView.setImageResource(iconResId)
                    switchWidget.isChecked = isChecked
                    switchWidget.setOnCheckedChangeListener { _, isChecked ->
                        onCheckedChange(isChecked)
                    }
                }
            }

            inner class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
        }


        binding.SettingsRecyclerView.layoutManager = LinearLayoutManager(requireActivity())




        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
