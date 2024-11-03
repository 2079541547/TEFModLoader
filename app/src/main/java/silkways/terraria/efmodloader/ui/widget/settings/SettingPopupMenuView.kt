package silkways.terraria.efmodloader.ui.widget.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import silkways.terraria.efmodloader.R

/**
 * 这是最后一个了哦～ love from YuWu 2024.11.03
 **/

class SettingPopupMenuView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val icon: ShapeableImageView
    private val title: MaterialTextView
    private val subtitle: MaterialTextView
    private val menuButton: MaterialButton

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_setting_popup_menu, this, true)

        icon = findViewById(R.id.icon)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        menuButton = findViewById(R.id.menu_button)

        // 设置点击事件
        setOnClickListener { v ->
            onItemClickListener?.invoke(v)
        }

        menuButton.setOnClickListener {
            showPopupMenu(it)
        }
    }

    var onItemClickListener: ((View) -> Unit)? = null
    var onMenuItemClickListener: PopupMenu.OnMenuItemClickListener? = null

    fun setIcon(iconResId: Int) {
        icon.setImageResource(iconResId)
    }

    fun setTitle(title: String) {
        this.title.text = title
    }

    fun setTitle(title: Int) {
        this.title.text = context.getString(title)
    }

    fun setSubtitle(subtitle: String) {
        this.subtitle.text = subtitle
    }

    fun setSubtitle(subtitle: Int) {
        this.subtitle.text = context.getString(subtitle)
    }

    fun setMenuButtonText(buttonText: String) {
        menuButton.text = buttonText
    }

    fun setMenuButtonText(buttonText: Int) {
        menuButton.text = context.getString(buttonText)
    }

    fun setMenuResource(menuResId: Int) {
        this.menuResId = menuResId
    }

    private var menuResId: Int = 0

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        val menuInflater = MenuInflater(context)
        menuInflater.inflate(menuResId, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            onMenuItemClickListener?.onMenuItemClick(item) == true
        }
        popupMenu.show()
    }
}