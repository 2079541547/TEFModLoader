package silkways.terraria.efmodloader

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import silkways.terraria.efmodloader.data.GameSettings
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.WelcomeMainBinding
import silkways.terraria.efmodloader.logic.ApplicationSettings
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File


class Welcome : AppCompatActivity() {

    private lateinit var binding: WelcomeMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        File("${this.getExternalFilesDir(null)}/ToolBoxData/").mkdirs()

        //创建配置
        JsonConfigModifier.createJsonConfig(this, Settings.jsonPath, Settings.Data)
        JsonConfigModifier.createJsonConfig(this, GameSettings.jsonPath, GameSettings.Data)
        JsonConfigModifier.updateJsonKeys(this, Settings.jsonPath, Settings.Data)
        JsonConfigModifier.updateJsonKeys(this, GameSettings.jsonPath, GameSettings.Data)

        ApplicationSettings.setupTheme(this)

        actionBar?.hide()
        binding = WelcomeMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 28) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setDisplayInNotch(this)

        val animation = AnimationUtils.loadAnimation(this, R.anim.bloom)
        binding.welcomeImage.startAnimation(animation)

        // 延时3秒后跳转到MainGameActivity
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                val intent = Intent(this@Welcome, MainActivity::class.java)
                startActivity(intent)
                finish() // 结束当前活动

            }
        }.start()


    }


    private fun setDisplayInNotch(activity: Activity) {
        val flag = 0x00000100 or 0x00000200 or 0x00000400
        try {
            val method = Window::class.java.getMethod(
                "addExtraFlags",
                Int::class.javaPrimitiveType
            )
            method.invoke(activity.window, flag)
        } catch (ignore: Exception) {
        }
    }


}
