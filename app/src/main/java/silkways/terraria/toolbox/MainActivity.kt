package silkways.terraria.toolbox

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.enableEdgeToEdge
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import silkways.terraria.toolbox.data.GameSettings
import silkways.terraria.toolbox.data.Settings
import silkways.terraria.toolbox.databinding.ActivityMainBinding
import silkways.terraria.toolbox.databinding.HomeDialogAgreementBinding
import silkways.terraria.toolbox.logic.JsonConfigModifier
import silkways.terraria.toolbox.logic.LanguageHelper
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader


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

        //创建配置
        JsonConfigModifier.createJsonConfig(this, Settings.jsonPath, Settings.Data)
        JsonConfigModifier.createJsonConfig(this, GameSettings.jsonPath, GameSettings.Data)
        checkAndWriteFile(this)

        //设置语言
        setupLanguage()

        //设置主题
        setupTheme()

        actionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        File("${this.getExternalFilesDir(null)}/ToolBoxData/ModData").mkdirs()
        File("${this.getExternalFilesDir(null)}/ToolBoxData/Resources").mkdirs()
        File("${this.getExternalFilesDir(null)}/ToolBoxData/APK").mkdirs()

        /*
        //无法使用啊QAQ

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         */

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

        setDisplayInNotch(this)


        // 获取 NavHostFragment，它是 Jetpack Navigation 的核心组件
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        // 获取 NavHostFragment 内的 NavController，用于控制界面间的导航
        navHostFragment.navController
        //navHostFragment.navController.navigate(R.id.navigation_terminal)

        val file = File("${this.cacheDir}/lspatch/origin/")
        val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
        copyFileIfNotExists("${this.cacheDir}/lspatch/origin/${files?.get(0)?.name}", "${this.getExternalFilesDir(null)}/ToolBoxData/APK/base.apk")

        if(!(JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.agreement) as Boolean)){
            showAgreement_Dialog(this)
        }


    }


    /*
    *请相信我，这段代码真的不是复制的QAQ
     */
    private fun showAgreement_Dialog(context: Context){

        var dialogBinding: HomeDialogAgreementBinding? = HomeDialogAgreementBinding.inflate(
            LayoutInflater.from(this))

        val builder = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setView(dialogBinding?.root)
            .setTitle(R.string.Agreement_title)


        builder.setPositiveButton(getString(R.string.Agreement_ok)) { dialog: DialogInterface, _: Int ->
            JsonConfigModifier.modifyJsonConfig(this, Settings.jsonPath, Settings.agreement, true)
            dialog.dismiss()
        }


        builder.setNegativeButton(getString(R.string.Agreement_no)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            finishAffinity()
        }

        val dialog = builder.create().apply {
            //设置窗口特性
            window?.let { dialogWindow ->
                setCanceledOnTouchOutside(false) // 设置触摸对话框外部不可取消
            }

            dialogBinding?.AgreementContent?.text = readFileFromAssets(LanguageHelper.getFileLanguage(JsonConfigModifier.readJsonValue(context, Settings.jsonPath, Settings.languageKey), context, "agreement", ""))

            // 设置对话框关闭监听器
            setOnDismissListener {
                dialogBinding = null // 毁尸灭迹（不是哥们
            }
        }

        dialog.show()
    }

    @Throws(IOException::class)
    private fun readFileFromAssets(fileName: String): String {
        val stringBuilder = StringBuilder()
        val inputStream = assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }

        reader.close()
        inputStream.close()

        return stringBuilder.toString()
    }

    private fun copyFileIfNotExists(sourcePath: String?, destinationPath: String?) {
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

    private fun setupLanguage() {
        var type: String = ""
        
        when(JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.languageKey)){
            0 -> {
                type = when(LanguageHelper.getLanguageAsNumber(this)) {
                    1, 2, 3 -> ""
                    4 -> "en"
                    else -> ({}).toString()
                }
            }
            1, 2, 3 -> type = ""
            4 -> type= "en"
        }
        LanguageHelper.setAppLanguage(this, type)
    }

    private fun setupTheme() {
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
