package silkways.terraria.efmodloader.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import silkways.terraria.efmodloader.databinding.ActivitySettingBinding
import silkways.terraria.efmodloader.ui.adapter.settings.SettingAdapter
import silkways.terraria.efmodloader.ui.adapter.settings.SettingItem
import kotlin.collections.mutableListOf
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.SettingsPacknameDialogBinding
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.utils.SPUtils

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var settings: MutableList<SettingItem>
    private lateinit var adapter: SettingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 RecyclerView
        val recyclerView = binding.SettingsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 初始化设置列表
        settings = mutableListOf()

        // 创建并设置适配器
        adapter = SettingAdapter(settings, this)
        recyclerView.adapter = adapter

        addSetting(SettingItem.Title(getString(R.string.important)))
        addSetting(SettingItem.PopupMenu(
            R.drawable.twotone_memory_24,
            getString(R.string.runtime),
            when (SPUtils.readInt(Settings.Runtime, 0)) {
                0 -> getString(R.string.runtime_1)
                1 -> getString(R.string.runtime_2)
                else -> getString(R.string.developer_Easteregg_4)
            },
            R.menu.settings_runtime,
        ) { menuItemId ->
            when (menuItemId) {
                R.id.menu_runtime_0 -> SPUtils.putInt(Settings.Runtime, 0)
                R.id.menu_runtime_1 -> SPUtils.putInt(Settings.Runtime, 1)
            }
        })

        addSetting(SettingItem.Button(
            R.drawable.twotone_workspaces_24,
            getString(R.string.TargetPackageName),
            SPUtils.readString(Settings.GamePackageName, "com.and.games505.TerrariaPaid").toString()
        ) {
            // 初始化Dialog的绑定对象
            var dialogBinding: SettingsPacknameDialogBinding? = SettingsPacknameDialogBinding.inflate(LayoutInflater.from(this))

            // 创建对话框构建器
            val builder = MaterialAlertDialogBuilder(this)
                .setCancelable(true)
                .setView(dialogBinding?.root)

            val dialog = builder.create().apply {
                // 设置对话框窗口属性
                window?.let { dialogWindow ->
                    setCanceledOnTouchOutside(true) // 设置触摸对话框外部可取消
                }


                dialogBinding?.button?.setOnClickListener {
                    SPUtils.putString(Settings.GamePackageName, dialogBinding?.TextInputEditText?.text.toString())
                    dismiss()
                }

                // 设置对话框关闭监听器
                setOnDismissListener {
                    dialogBinding = null
                }
            }

            dialog.show()
        })

        addSetting(SettingItem.Title(getString(R.string.settings_1)))

        addSetting(SettingItem.PopupMenu(
            R.drawable.twotone_language_24,
            getString(R.string.settings_language),
            when (SPUtils.readInt(Settings.languageKey, 0)) {
                1 -> getString(R.string.settings_language_2)
                2 -> getString(R.string.settings_language_3)
                3 -> getString(R.string.settings_language_4)
                4 -> getString(R.string.settings_language_5)
                else -> getString(R.string.settings_language_1)
            },
            R.menu.settings_language_menu
        ) { menuItemId ->
            when (menuItemId) {
                R.id.menu_language_1 -> setLanguage(0)
                R.id.menu_language_2 -> setLanguage(1)
                R.id.menu_language_3 -> setLanguage(2)
                R.id.menu_language_4 -> setLanguage(3)
                R.id.menu_language_5 -> setLanguage(4)
            }
        })

// 主题设置
        addSetting(SettingItem.PopupMenu(
            R.drawable.twotone_color_lens_24,
            getString(R.string.settings_theme),
            when (SPUtils.readInt(Settings.themeKey, -1)) {
                1 -> getString(R.string.settings_theme_2)
                2 -> getString(R.string.settings_theme_3)
                else -> getString(R.string.settings_theme_1)
            },
            R.menu.settings_theme_menu
        ) { menuItemId ->
            when (menuItemId) {
                R.id.menu_theme_1 -> {
                    SPUtils.putInt(Settings.themeKey, -1)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                R.id.menu_theme_2 -> {
                    SPUtils.putInt(Settings.themeKey, 1)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                R.id.menu_theme_3 -> {
                    SPUtils.putInt(Settings.themeKey, 2)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        })
    }


    private fun setLanguage(code: Int) {
        SPUtils.putInt(Settings.languageKey, code)
        LanguageHelper.setAppLanguage(this, LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), this))

        val builder = MaterialAlertDialogBuilder(this)

        builder.setTitle(getString(R.string.RestartDialog_title))

        builder.setPositiveButton(getString(R.string.RestartApp)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // 清除到这个Activity之上的所有Activity
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 以新的任务栈项的形式启动
            startActivity(intent)
        }

        builder.setNegativeButton(getString(R.string.close)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        val dialog: Dialog = builder.create()
        dialog.show()
    }

    /**
     * 动态添加设置项
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addSetting(vararg settingItems: SettingItem) {
        settings.addAll(settingItems)
        adapter.notifyDataSetChanged()
    }
}