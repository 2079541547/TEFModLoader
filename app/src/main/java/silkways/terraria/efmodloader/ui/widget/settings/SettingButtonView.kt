package silkways.terraria.efmodloader.ui.widget.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import silkways.terraria.efmodloader.R

/**
 * 别看辣！ love from YuWu 2024.11.03
 **/

class SettingButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val icon: ShapeableImageView
    private val title: MaterialTextView
    private val subtitle: MaterialTextView
    private val button: MaterialButton

    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_setting_button, this, true)

        icon = findViewById(R.id.icon)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        button = findViewById(R.id.button)

        // 设置点击事件
        setOnClickListener { v ->
            onItemClickListener?.invoke(v)
        }
    }

    var onItemClickListener: ((View) -> Unit)? = null

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

    fun setButtonText(buttonText: String) {
        button.text = buttonText
    }

    fun setButtonText(buttonText: Int) {
        button.text = context.getString(buttonText)
    }

    fun setVisible (Visible: Int) {
        button.visibility = Visible
    }

    fun setOnClickListener(listener: ((View) -> Unit)) {
        button.setOnClickListener { v ->
            listener(v)
        }
    }
}