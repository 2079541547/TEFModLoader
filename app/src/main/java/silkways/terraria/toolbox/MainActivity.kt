package silkways.terraria.toolbox

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import silkways.terraria.toolbox.data.Settings
import silkways.terraria.toolbox.databinding.ActivityMainBinding
import silkways.terraria.toolbox.logic.JsonConfigModifier
import silkways.terraria.toolbox.logic.LanguageHelper
import java.io.File
import java.util.Locale


/**
 * MainActivity 是应用的主要入口点，继承自 AppCompatActivity。
 * 它使用 Jetpack Navigation 和 Data Binding 库来管理界面导航和视图绑定。
 */
class MainActivity : AppCompatActivity() {

    /**
     * ActivityMainBinding 的一个延迟初始化变量，用于绑定布局文件 activity_main.xml。
     * 这个绑定对象提供了对 UI 组件的直接访问。
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * 当活动创建时调用，负责设置布局和初始化必要的组件。
     *
     * - 使用 `inflate` 方法将 activity_main.xml 布局文件加载到 layoutInflater 中。
     * - 隐藏状态栏和导航栏，实现全屏显示。
     * - 将加载后的布局设置为活动的内容视图。
     * - 获取 NavHostFragment 并将其 navController 用于导航操作。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //设置语言
        when(JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.languageKey)){
            0 -> {
                when(LanguageHelper.getLanguageAsNumber(this)){
                    2 -> LanguageHelper.setAppLanguage(this, "")
                    3 -> LanguageHelper.setAppLanguage(this, "")
                    4 -> LanguageHelper.setAppLanguage(this, "en")
                }
            }

            2 -> LanguageHelper.setAppLanguage(this, "")
            3 -> LanguageHelper.setAppLanguage(this, "")
            4 -> LanguageHelper.setAppLanguage(this, "en")
        }



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


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        File("${this.getExternalFilesDir(null)}/ToolBoxData/ModData").mkdirs()
        File("${this.getExternalFilesDir(null)}/ToolBoxData/Resources").mkdirs()
        File("${this.getExternalFilesDir(null)}/ToolBoxData/bak").mkdirs()


        actionBar?.hide()

        if (Build.VERSION.SDK_INT >= 28) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        setDisplayInNotch(this)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)




        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // 获取 NavHostFragment，它是 Jetpack Navigation 的核心组件
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        // 获取 NavHostFragment 内的 NavController，用于控制界面间的导航
        navHostFragment.navController
        //navHostFragment.navController.navigate(R.id.navigation_terminal)



        JsonConfigModifier.createJsonConfig(this, Settings.jsonPath, Settings.SettingsData);
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


}
