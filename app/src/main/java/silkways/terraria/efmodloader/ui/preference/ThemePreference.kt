package silkways.terraria.efmodloader.ui.preference

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.PopupMenu
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.utils.SPUtils

class ThemePreference(context: Context, attrs: AttributeSet) : Preference(context, attrs) {

    init {
        layoutResource = R.layout.preference_popup_menu
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)

        val titleView: TextView = holder.findViewById(android.R.id.title) as TextView
        titleView.text = title

        val iconView: ImageView = holder.findViewById(R.id.icon) as ImageView
        iconView.setImageDrawable(icon)

        // 获取持久化的值并更新 summary
        val persistedValue = getPersistedString(null)
        summary = persistedValue ?: "Select an option"

        holder.itemView.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(context, anchor)

        // 动态添加菜单项
        popupMenu.menuInflater.inflate(R.menu.settings_theme_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            val selectedOption = menuItem.title
            // 更新 summary
            selectedOption?.let {
                summary = it
                persistString(it.toString()) // 持久化存储选择的值
            }
            // 获取选择的项
            when (menuItem.itemId) {
                R.id.menu_theme_1 -> {
                    setTheme(-1)
                    true
                }
                R.id.menu_theme_2 -> {
                    setTheme(1)
                    true
                }
                R.id.menu_theme_3 -> {
                    setTheme(2)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    fun setTheme(key: Int) {
        SPUtils.putInt(Settings.themeKey, key)
        AppCompatDelegate.setDefaultNightMode(key)
    }
}