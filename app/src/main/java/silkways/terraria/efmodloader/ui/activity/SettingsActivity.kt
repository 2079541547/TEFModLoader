package silkways.terraria.efmodloader.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import silkways.terraria.efmodloader.databinding.ActivitySettingBinding

class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)

        setContentView(binding.root)





//        data class SettingTitle(val title: String)
//        data class SettingButton(val title: String, val subTitle: String, val iconResId: Int, val onClick: (View) -> Unit)
//        data class SettingSwitch(val title: String, val subTitle: String, val iconResId: Int, val isChecked: Boolean, val onCheckedChange: (Boolean) -> Unit)
//        data class Divider(val isDivider: Boolean = true)
//
//        val activity: Activity = this
//
//        binding.SettingsRecyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//            private val VIEW_TYPE_TITLE = 0
//            private val VIEW_TYPE_BUTTON = 1
//            private val VIEW_TYPE_SWITCH = 2
//            private val VIEW_TYPE_DIVIDER = 3
//
//
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//                val inflater = LayoutInflater.from(parent.context)
//                return when (viewType) {
//                    VIEW_TYPE_TITLE -> TitleViewHolder(inflater.inflate(R.layout.item_title, parent, false))
//                    VIEW_TYPE_BUTTON -> ButtonViewHolder(inflater.inflate(R.layout.home_setting_item1, parent, false))
//                    VIEW_TYPE_SWITCH -> SwitchViewHolder(inflater.inflate(R.layout.home_setting_item2, parent, false))
//                    VIEW_TYPE_DIVIDER -> DividerViewHolder(inflater.inflate(R.layout.item_divider, parent, false))
//                    else -> throw IllegalArgumentException("Unknown view type")
//                }
//            }
//
//
//            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//                when (holder) {
//                    is TitleViewHolder -> {
//                        val settingTitle = settingsList[position] as SettingTitle
//                        holder.bind(settingTitle.title)
//                    }
//                    is ButtonViewHolder -> {
//                        val settingButton = settingsList[position] as SettingButton
//                        holder.bind(settingButton.title, settingButton.subTitle, settingButton.iconResId, settingButton.onClick)
//                    }
//                    is SwitchViewHolder -> {
//                        val settingSwitch = settingsList[position] as SettingSwitch
//                        holder.bind(settingSwitch.title, settingSwitch.subTitle, settingSwitch.iconResId, settingSwitch.isChecked, settingSwitch.onCheckedChange)
//                    }
//                    is DividerViewHolder -> {}
//                }
//            }
//
//
//            private fun getTheme_text(): String {
//                when(SPUtils.readInt(Settings.themeKey, -1)) {
//                    -1 -> return getString(R.string.settings_theme_1)
//                    1 -> return getString(R.string.settings_theme_2)
//                    2 -> return getString(R.string.settings_theme_3)
//                }
//                return "错误"
//            }
//
//            private fun getLanguage_Text(): String {
//                when(SPUtils.readInt(Settings.languageKey, -1)) {
//                    0 -> return getString(R.string.settings_language_1)
//                    1 -> return getString(R.string.settings_language_2)
//                    2 -> return getString(R.string.settings_language_3)
//                    3 -> return getString(R.string.settings_language_4)
//                    4 -> return getString(R.string.settings_language_5)
//                }
//                return "错误"
//            }
//
//            fun restartApp(context: Context) {
//                val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
//                intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 清除到这个Activity之上的所有Activity
//                intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 以新的任务栈项的形式启动
//                context.startActivity(intent)
//            }
//
//            fun showRestartDialog(){
//                val builder = MaterialAlertDialogBuilder(activity)
//
//                builder.setTitle(getString(R.string.RestartDialog_title))
//
//                builder.setPositiveButton(getString(R.string.RestartApp)) { dialog: DialogInterface, _: Int ->
//                    dialog.dismiss()
//                    restartApp(activity)
//                }
//
//                builder.setNegativeButton(getString(R.string.close)) { dialog: DialogInterface, _: Int ->
//                    dialog.dismiss()
//                }
//
//                val dialog: Dialog = builder.create()
//                dialog.show()
//            }
//
//
//            private val settingsList = listOf(
//
//                //设置标题
//                SettingTitle(getString(R.string.settings_1)),
//
//
//                //主题设置
//                SettingButton(getString(R.string.settings_theme), getTheme_text(), R.drawable.twotone_color_lens_24) {
//
//                    val popupMenu = PopupMenu(activity, it)
//                    popupMenu.menuInflater.inflate(R.menu.settings_theme_menu, popupMenu.menu)
//
//                    popupMenu.setOnMenuItemClickListener { menuItem ->
//                        fun setTheme(key: Int) {
//                            SPUtils.putInt(Settings.themeKey, key)
//                            AppCompatDelegate.setDefaultNightMode(key)
//                        }
//                        when (menuItem.itemId) {
//                            R.id.menu_theme_1 -> {
//                                setTheme(-1)
//                                true
//                            }
//                            R.id.menu_theme_2 -> {
//                                setTheme(1)
//                                true
//                            }
//                            R.id.menu_theme_3 -> {
//                                setTheme(2)
//                                true
//                            }
//                            else -> false
//                        }
//
//                    }
//                    popupMenu.show()
//                },
//
//                //语言设置
//                SettingButton(getString(R.string.settings_language), getLanguage_Text(), R.drawable.twotone_language_24) {
//                    val popupMenu = PopupMenu(activity, it)
//                    popupMenu.menuInflater.inflate(R.menu.settings_language_menu, popupMenu.menu)
//
//                    popupMenu.setOnMenuItemClickListener { menuItem ->
//                        when (menuItem.itemId) {
//                            R.id.menu_language_1 -> {
//                                JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.languageKey, 0)
//                                showRestartDialog()
//                                true
//                            }
//                            R.id.menu_language_2 -> {
//                                JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.languageKey, 1)
//                                showRestartDialog()
//                                true
//                            }
//                            R.id.menu_language_3 -> {
//                                JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.languageKey, 2)
//                                showRestartDialog()
//                                true
//                            }
//                            R.id.menu_language_4 -> {
//                                JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.languageKey, 3)
//                                showRestartDialog()
//                                true
//                            }
//                            R.id.menu_language_5 -> {
//                                JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.languageKey, 4)
//                                showRestartDialog()
//                                true
//                            }
//                            else -> false
//                        }
//                    }
//                    popupMenu.show()
//                },
//
//                SettingSwitch(getString(R.string.Directly_replace_the_file), getString(R.string.Directly_replace_the_file_subtitle),
//                    R.drawable.twotone_auto_awesome_motion_24, JsonConfigModifier.readJsonValue(activity, Settings.jsonPath, Settings.CoveringFiles) as Boolean) { isChecked ->
//                    JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.CoveringFiles, isChecked)
//                },
//
//                SettingSwitch(getString(R.string.Automatically_clear_cache), getString(R.string.Automatically_clear_cache_subTitle),
//                    R.drawable.twotone_cleaning_services_24, JsonConfigModifier.readJsonValue(activity, Settings.jsonPath, Settings.autoClean) as Boolean) { isChecked ->
//                    JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.autoClean, isChecked)
//                },
//
//                SettingSwitch(getString(R.string.clear_dialogs), getString(R.string.clear_dialogs_subtitle),
//                    R.drawable.twotone_clear_all_24, JsonConfigModifier.readJsonValue(activity, Settings.jsonPath, Settings.CleanDialog) as Boolean) { isChecked ->
//                    JsonConfigModifier.modifyJsonConfig(activity, Settings.jsonPath, Settings.CleanDialog, isChecked)
//                },
//
//                //分割线
//                //Divider()
//
//
//            )
//            override fun getItemCount(): Int {
//                return settingsList.size
//            }
//
//
//            override fun getItemViewType(position: Int): Int {
//                return when (settingsList[position]) {
//                    is SettingTitle -> VIEW_TYPE_TITLE
//                    is SettingButton -> VIEW_TYPE_BUTTON
//                    is SettingSwitch -> VIEW_TYPE_SWITCH
//                    is Divider -> VIEW_TYPE_DIVIDER
//                    else -> throw IllegalArgumentException("Unknown item type")
//                }
//            }
//
//
//            inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//                private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
//
//                fun bind(title: String) {
//                    titleTextView.text = title
//                    titleTextView.setTextColor(ContextCompat.getColor(activity, com.google.android.material.R.color.material_dynamic_primary50))
//                }
//            }
//
//            inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//                private val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
//                private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
//                private val subTitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)
//
//                fun bind(title: String, subTitle: String, iconResId: Int, onClick: (View) -> Unit) {
//                    titleTextView.text = title
//                    subTitleTextView.text = subTitle
//                    iconImageView.setImageResource(iconResId)
//                    itemView.setOnClickListener { it ->
//                        onClick(it)
//                    }
//                }
//            }
//
//            inner class SwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//                private val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
//                private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
//                private val subTitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)
//                private val switchWidget: MaterialSwitch = itemView.findViewById(R.id.Setting_Switch)
//
//                fun bind(title: String, subTitle: String, iconResId: Int, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
//                    titleTextView.text = title
//                    subTitleTextView.text = subTitle
//                    iconImageView.setImageResource(iconResId)
//                    switchWidget.isChecked = isChecked
//                    switchWidget.setOnCheckedChangeListener { _, isChecked ->
//                        onCheckedChange(isChecked)
//                    }
//                }
//            }
//
//            inner class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
//        }
//
//
//        binding.SettingsRecyclerView.layoutManager = LinearLayoutManager(activity)


    }
}
