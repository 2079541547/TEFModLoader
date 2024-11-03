package silkways.terraria.efmodloader.ui.adapter.settings

import android.view.View

sealed class SettingItem {
    /**
     * 表示一个按钮类型的设置项。
     *
     * @param iconResId 图标资源，用于显示按钮旁边的图标。
     * @param title 标题字符串资源，用于显示按钮的标题。
     * @param subtitle 副标题字符串资源，用于显示按钮的副标题。
     * @param onClick 点击事件处理函数，当用户点击按钮时会被调用。
     */
    data class Button(
        val iconResId: Int,
        val title: String,
        val subtitle: String,
        val onClick: (View) -> Unit
    ) : SettingItem()

    /**
     * 表示一个弹出菜单类型的设置项。
     *
     * @param iconResId 图标资源，用于显示弹出菜单旁边的图标。
     * @param title 标题字符串资源，用于显示弹出菜单的标题。
     * @param subtitle 副标题字符串资源，用于显示弹出菜单的副标题。
     * @param buttonText 对应按钮字符串资源，用于点击时弹出菜单。
     * @param menuResId 菜单资源 ID，用于定义弹出菜单中的菜单项。
     * @param onMenuItemClick 菜单项点击事件处理函数，当用户点击菜单项时会被调用，参数是被点击的菜单项的 ID。
     */
    data class PopupMenu(
        val iconResId: Int,
        val title: String,
        val subtitle: String,
        val buttonText: String,
        val menuResId: Int,
        val onMenuItemClick: (Int) -> Unit
    ) : SettingItem()

    /**
     * 表示一个开关类型的设置项。
     *
     * @param iconResId 图标资源，用于显示开关旁边的图标。
     * @param title 标题字符串资源，用于显示开关的标题。
     * @param subtitle 副标题字符串资源，用于显示开关的副标题。
     * @param isChecked 开关的初始状态，true 表示开启，false 表示关闭。
     * @param onCheckedChange 开关状态改变事件处理函数，当用户切换开关状态时会被调用，参数是新的开关状态。
     */
    data class Switch(
        val iconResId: Int,
        val title: String,
        val subtitle: String,
        val isChecked: Boolean,
        val onCheckedChange: (Boolean) -> Unit
    ) : SettingItem()

    /**
     * 表示一个分割线类型的设置项。
     *
     * @param isDivider 是否显示分割线，默认为 true。
     */
    data class Divider(val isDivider: Boolean = true) : SettingItem()

    /**
     * 表示一个标题类型的设置项。
     *
     * @param title 标题字符串资源，用于显示设置项的标题。
     */
    data class Title(val title: String) : SettingItem()
}