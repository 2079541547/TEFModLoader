package silkways.terraria.toolbox

import android.app.Activity
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import silkways.terraria.toolbox.data.Settings
import silkways.terraria.toolbox.databinding.WelcomeMainBinding
import silkways.terraria.toolbox.logic.JsonConfigModifier
import java.io.IOException


class Welcome : AppCompatActivity() {

    private lateinit var binding: WelcomeMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //设置主题
        when(JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.themeKey)){
            0 -> {
                val isDarkModeEnabled = AppCompatDelegate.getDefaultNightMode()
                if (isDarkModeEnabled == AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }else if(isDarkModeEnabled == AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

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

                printAssets()
            }
        }.start()


    }


    fun setDisplayInNotch(activity: Activity) {
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


    fun listAssets(assetManager: AssetManager, path: String): List<String> {
        try {
            // 获取指定路径下的所有文件名
            val files = assetManager.list(path)
            val result: MutableList<String> = ArrayList()
            for (file in files!!) {
                // 拼接完整的路径
                val fullPath = if (path.isEmpty()) file else "$path/$file"
                result.add(fullPath)

                // 再次尝试获取这个路径下的所有文件名，如果返回值不为空则认为这是一个目录
                if (assetManager.list(fullPath)!!.size > 0) {
                    // 如果是目录，则递归调用listAssets
                    result.addAll(listAssets(assetManager, fullPath))
                }
            }
            return result
        } catch (e: IOException) {
            e.printStackTrace()
            return ArrayList()
        }
    }

    fun printAssets() {
        val assetManager = resources.assets
        try {
            // 列出assets根目录下的所有文件和目录
            val assets = listAssets(assetManager, "")
            for (asset in assets) {
                println("Asset: $asset")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}
