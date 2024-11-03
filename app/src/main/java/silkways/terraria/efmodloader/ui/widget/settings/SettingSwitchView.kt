package silkways.terraria.efmodloader.ui.widget.settings

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.utils.SPUtils

/**
 * 前端来玩Kotlin捏 love from YuWu 2024.11.03
 **/

class SettingSwitchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val icon: ShapeableImageView
    private val title: MaterialTextView
    private val subtitle: MaterialTextView
    private val switch: MaterialSwitch

    init {
        LayoutInflater.from(context).inflate(R.layout.view_setting_switch, this, true)

        icon = findViewById(R.id.icon)
        title = findViewById(R.id.title)
        subtitle = findViewById(R.id.subtitle)
        switch = findViewById(R.id.Setting_Switch)

        setOnClickListener { v ->
            onItemClickListener?.invoke(v)
        }
    }

    var onItemClickListener: ((View) -> Unit)? = null

    fun setIcon(iconResId: Int) {
        icon.setImageResource(iconResId)
    }

    fun setTitle(title: CharSequence) {
        this.title.text = title
    }

    fun setSubtitle(subtitle: CharSequence) {
        this.subtitle.text = subtitle
    }

    fun setSwitchChecked(checked: Boolean) {
        switch.isChecked = checked
    }

    fun setSwitchChecked(checked: String) {
        switch.isChecked = SPUtils.readBoolean(checked, false)
    }

    fun setOnCheckedChangeListener(listener: ((Boolean) -> Unit)) {
        switch.setOnCheckedChangeListener { _, isChecked ->
            listener(isChecked)
        }
    }
}