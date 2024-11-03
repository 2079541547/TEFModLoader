package silkways.terraria.efmodloader.ui.adapter.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.ui.adapter.settings.SettingItem.Button
import silkways.terraria.efmodloader.ui.adapter.settings.SettingItem.Divider
import silkways.terraria.efmodloader.ui.adapter.settings.SettingItem.Switch
import silkways.terraria.efmodloader.ui.adapter.settings.SettingItem.Title

/**
 * 设置项适配器，用于在 RecyclerView 中显示不同的设置项。
 *
 * @param settings 设置项列表。
 * @param context 上下文对象，用于访问资源。
 */
class SettingAdapter(
    private val settings: List<Any>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_TITLE = 0
    private val VIEW_TYPE_DIVIDER = 1
    private val VIEW_TYPE_BUTTON = 2
    private val VIEW_TYPE_SWITCH = 3
    private val VIEW_TYPE_POPUPMENU = 4

    /**
     * 创建新的 ViewHolder。
     *
     * @param parent 父视图组。
     * @param viewType 视图类型。
     * @return 新创建的 ViewHolder。
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TITLE -> TitleViewHolder(inflater.inflate(R.layout.item_title, parent, false))
            VIEW_TYPE_DIVIDER -> DividerViewHolder(inflater.inflate(R.layout.item_divider, parent, false))
            VIEW_TYPE_BUTTON -> ButtonViewHolder(inflater.inflate(R.layout.view_setting_button, parent, false))
            VIEW_TYPE_SWITCH -> SwitchViewHolder(inflater.inflate(R.layout.view_setting_switch, parent, false))
            VIEW_TYPE_POPUPMENU -> PopupMenuViewHolder(inflater.inflate(R.layout.view_setting_popup_menu, parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    /**
     * 绑定 ViewHolder 到指定位置的数据。
     *
     * @param holder ViewHolder。
     * @param position 当前位置。
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TitleViewHolder -> {
                val settingTitle = settings[position] as Title
                holder.bind(settingTitle.title)
            }
            is ButtonViewHolder -> {
                val settingButton = settings[position] as Button
                holder.bind(settingButton.title, settingButton.subtitle, settingButton.iconResId, settingButton.onClick)
            }
            is SwitchViewHolder -> {
                val settingSwitch = settings[position] as Switch
                holder.bind(settingSwitch.title, settingSwitch.subtitle, settingSwitch.iconResId, settingSwitch.isChecked, settingSwitch.onCheckedChange)
            }
            is PopupMenuViewHolder -> {
                val settingPopupMenu = settings[position] as SettingItem.PopupMenu
                holder.bind(settingPopupMenu.title, settingPopupMenu.subtitle, settingPopupMenu.iconResId, settingPopupMenu.menuResId, settingPopupMenu.buttonText, settingPopupMenu.onMenuItemClick)
            }
            is DividerViewHolder -> {}
        }
    }

    /**
     * 返回设置项的数量。
     *
     * @return 设置项的数量。
     */
    override fun getItemCount(): Int {
        return settings.size
    }

    /**
     * 获取指定位置的视图类型。
     *
     * @param position 当前位置。
     * @return 视图类型。
     */
    override fun getItemViewType(position: Int): Int {
        return when (settings[position]) {
            is Title -> VIEW_TYPE_TITLE
            is Button -> VIEW_TYPE_BUTTON
            is Switch -> VIEW_TYPE_SWITCH
            is Divider -> VIEW_TYPE_DIVIDER
            is SettingItem.PopupMenu -> VIEW_TYPE_POPUPMENU
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    /**
     * 标题视图的 ViewHolder。
     */
    inner class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)

        /**
         * 绑定标题数据。
         *
         * @param title 标题字符串资源 ID。
         */
        fun bind(title: Int) {
            titleTextView.text = context.getString(title)
        }
    }

    /**
     * 分割线视图的 ViewHolder。
     */
    inner class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     * 按钮视图的 ViewHolder。
     */
    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
        private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
        private val subTitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)
        private val button: MaterialButton = itemView.findViewById(R.id.button)

        /**
         * 绑定按钮数据。
         *
         * @param title 标题字符串资源 ID。
         * @param subtitle 副标题字符串资源 ID。
         * @param iconResId 图标资源 ID。
         * @param onClick 点击事件处理函数。
         */
        fun bind(title: Int, subtitle: Int, iconResId: Int, onClick: (View) -> Unit) {
            button.visibility = View.GONE
            titleTextView.text = context.getString(title)
            subTitleTextView.text = context.getString(subtitle)
            iconImageView.setImageResource(iconResId)
            itemView.setOnClickListener { it ->
                onClick(it)
            }
        }
    }

    /**
     * 开关视图的 ViewHolder。
     */
    inner class SwitchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
        private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
        private val subTitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)
        private val switchWidget: MaterialSwitch = itemView.findViewById(R.id.Setting_Switch)

        /**
         * 绑定开关数据。
         *
         * @param title 标题字符串资源 ID。
         * @param subtitle 副标题字符串资源 ID。
         * @param iconResId 图标资源 ID。
         * @param isChecked 开关的初始状态。
         * @param onCheckedChange 开关状态改变事件处理函数。
         */
        fun bind(title: Int, subtitle: Int, iconResId: Int, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
            titleTextView.text = context.getString(title)
            subTitleTextView.text = context.getString(subtitle)
            iconImageView.setImageResource(iconResId)
            switchWidget.isChecked = isChecked
            switchWidget.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChange(isChecked)
            }
        }
    }

    /**
     * 弹出菜单视图的 ViewHolder。
     */
    inner class PopupMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val iconImageView: ShapeableImageView = itemView.findViewById(R.id.icon)
        private val titleTextView: MaterialTextView = itemView.findViewById(R.id.title)
        private val subTitleTextView: MaterialTextView = itemView.findViewById(R.id.subtitle)
        private val menuButton: MaterialButton = itemView.findViewById(R.id.menu_button)

        /**
         * 绑定弹出菜单数据。
         *
         * @param title 标题字符串资源 ID。
         * @param subtitle 副标题字符串资源 ID。
         * @param iconResId 图标资源 ID。
         * @param buttonText 按钮文本字符串资源 ID。
         * @param menuResId 菜单资源 ID。
         * @param onMenuItemClick 菜单项点击事件处理函数。
         */
        fun bind(
            title: Int,
            subtitle: Int,
            iconResId: Int,
            buttonText: Int,
            menuResId: Int,
            onMenuItemClick: (Int) -> Unit
        ) {
            titleTextView.text = context.getString(title)
            subTitleTextView.text = context.getString(subtitle)
            iconImageView.setImageResource(iconResId)
            menuButton.text = context.getString(buttonText)
            menuButton.setOnClickListener {
                showPopupMenu(it, menuResId, onMenuItemClick)
            }
        }

        /**
         * 显示弹出菜单。
         *
         * @param view 触发弹出菜单的视图。
         * @param menuResId 菜单资源 ID。
         * @param onMenuItemClick 菜单项点击事件处理函数。
         */
        private fun showPopupMenu(view: View, menuResId: Int, onMenuItemClick: (Int) -> Unit) {
            val popupMenu = PopupMenu(context, view)
            val menuInflater = MenuInflater(context)
            menuInflater.inflate(menuResId, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                onMenuItemClick(item.itemId)
                true
            }
            popupMenu.show()
        }
    }
}