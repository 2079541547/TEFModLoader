package silkways.terraria.toolbox

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import silkways.terraria.toolbox.data.GameSettings
import silkways.terraria.toolbox.data.Settings
import silkways.terraria.toolbox.databinding.ActivityMainBinding
import silkways.terraria.toolbox.logic.JsonConfigModifier
import silkways.terraria.toolbox.logic.LanguageHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


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
        setupLanguage()

        //设置主题
        setupTheme()
        actionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        File("${this.getExternalFilesDir(null)}/ToolBoxData/ModData").mkdirs()
        File("${this.getExternalFilesDir(null)}/ToolBoxData/Resources").mkdirs()
        File("${this.getExternalFilesDir(null)}/ToolBoxData/bak").mkdirs()
        File("${this.getExternalFilesDir(null)}/ToolBoxData/APK").mkdirs()

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

        //创建配置
        JsonConfigModifier.createJsonConfig(this, Settings.jsonPath, Settings.Data)
        JsonConfigModifier.createJsonConfig(this, GameSettings.jsonPath, GameSettings.Data)
        checkAndWriteFile(this)

        val file = File("${this.cacheDir}/lspatch/origin/")
        val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
        copyFileIfNotExists("${this.cacheDir}/lspatch/origin/${files?.get(0)?.name}", "${this.getExternalFilesDir(null)}/ToolBoxData/APK/base.apk")
    }


    fun copyFileIfNotExists(sourcePath: String?, destinationPath: String?) {
        val sourceFile = File(sourcePath)
        val destFile = File(destinationPath)
        // 检查目标文件是否已经存在
        if (destFile.exists()) {
            println("文件已存在")
            // 文件已存在，不做任何操作
            return
        }
        try {
            FileInputStream(sourceFile).use { fis ->
                FileOutputStream(destFile).use { fos ->
                    fis.channel.use { inputChannel ->
                        fos.channel.use { outputChannel ->

                            // 直接使用FileChannel进行高效复制
                            inputChannel.transferTo(0, inputChannel.size(), outputChannel)
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun checkAndWriteFile(context: Context) {
        val content = "[]"
        val file = File("${context.getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json")

        if (!file.exists()) {
            try {
                file.createNewFile()
                file.writeText(content)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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

    fun setupLanguage() {
        var type: String = ""
        
        when(JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.languageKey)){
            0 -> {
                type = when(LanguageHelper.getLanguageAsNumber(this)) {
                    1, 2, 3 -> ""
                    4 -> "en"
                }
            }
            1, 2, 3 -> type = ""
            4 -> type= "en"
        }
        LanguageHelper.setAppLanguage(this, type)
    }

    fun setupTheme() {
        when(JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.themeKey)) {
            0 -> {
                val isDarkModeEnabled = AppCompatDelegate.getDefaultNightMode()
                if (isDarkModeEnabled == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }else if(isDarkModeEnabled == AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }
}
