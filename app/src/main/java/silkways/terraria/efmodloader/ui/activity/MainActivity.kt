package silkways.terraria.efmodloader.ui.activity

import android.app.Dialog
import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.ActivityMainBinding
import silkways.terraria.efmodloader.databinding.HomeDialogAgreementBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils


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

    private var backPressedTime: Long = 0
    private val timeInterval: Long = 2000 // 设置两次按键之间的时间间隔（毫秒）

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

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 获取 NavHostFragment，它是 Jetpack Navigation 的核心组件
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        // 在底部导航栏上设置导航控制器
        binding.navView.setupWithNavController(navHostFragment.navController)

        if (!(JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.agreement) as Boolean)) {
            showAgreementDialog(this)
        }

        FileUtils.clearCache()

        // 注册 onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this) { handleBackPress() }
    }

    private fun handleBackPress() {
        if (SystemClock.elapsedRealtime() - backPressedTime < timeInterval) {
            if (SPUtils.readBoolean(Settings.autoClean, false)) {
                FileUtils.clearCache()
                finishAffinity()
            } else if (SPUtils.readBoolean(Settings.CleanDialog, true)) {
                showCleanDialog()
            } else {
                finishAffinity()
            }
        } else {
            backPressedTime = SystemClock.elapsedRealtime()
            val snackbar = Snackbar.make(binding.root, R.string.onBackPressedDispatcher_exit, Snackbar.LENGTH_SHORT)
            snackbar.anchorView = binding.navView
            snackbar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackbar.show()
        }
    }

    private fun showCleanDialog(){
        val builder = MaterialAlertDialogBuilder(this)

        builder.setTitle(getString(R.string.Clear_cache_title))
        builder.setMessage(getString(R.string.Clear_cache_message))

        builder.setPositiveButton(getString(R.string.Clear_cache)) { dialog: DialogInterface, _: Int ->
            FileUtils.clearCache()
            dialog.dismiss()
            finishAffinity()
        }

        builder.setNegativeButton(getString(R.string.NOClear_cache)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            finishAffinity()
        }

        val dialog: Dialog = builder.create()
        dialog.show()

    }

    private fun checkPermission() {
        val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (ContextCompat.checkSelfPermission(this, readPermission) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, writePermission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(readPermission, writePermission), 1001)
        }
    }

    /*
    *请相信我，这段代码真的不是复制的QAQ
     */
    private fun showAgreementDialog(context: Context) {

        var dialogBinding: HomeDialogAgreementBinding? = HomeDialogAgreementBinding.inflate(
            LayoutInflater.from(this)
        )

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

            dialogBinding?.AgreementContent?.text = FileUtils.readFileFromAssets(
                LanguageHelper.getFileLanguage(
                    JsonConfigModifier.readJsonValue(
                        context,
                        Settings.jsonPath,
                        Settings.languageKey
                    ), context, "agreement", ""
                )
            )

            // 设置对话框关闭监听器
            setOnDismissListener {
                dialogBinding = null // 毁尸灭迹（不是哥们
            }
        }

        dialog.show()
    }


}
