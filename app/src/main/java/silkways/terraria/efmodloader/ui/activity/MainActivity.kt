package silkways.terraria.efmodloader.ui.activity

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.Settings
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
import silkways.terraria.efmodloader.data.Settings.CleanDialog
import silkways.terraria.efmodloader.data.Settings.agreement
import silkways.terraria.efmodloader.data.Settings.autoClean
import silkways.terraria.efmodloader.data.Settings.jsonPath
import silkways.terraria.efmodloader.data.Settings.languageKey
import silkways.terraria.efmodloader.databinding.ActivityMainBinding
import silkways.terraria.efmodloader.databinding.HomeDialogAgreementBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.utils.SPUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0
    private val timeInterval: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setAppLanguage(this, LanguageHelper.getAppLanguage(SPUtils.readInt(silkways.terraria.efmodloader.data.Settings.languageKey, 0), this))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        binding.navView.setupWithNavController(navHostFragment.navController)

        if (!SPUtils.readBoolean(agreement, false)) {
            showAgreementDialog(this)
        }

        onBackPressedDispatcher.addCallback(this) { handleBackPress() }
        checkPermission()
    }

    private fun handleBackPress() {
        if (SystemClock.elapsedRealtime() - backPressedTime < timeInterval) {
            if (SPUtils.readBoolean(autoClean, false)) {
                FileUtils.clearCache()
                finishAffinity()
            } else if (SPUtils.readBoolean(CleanDialog, true)) {
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

    private fun showCleanDialog() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(getString(R.string.Clear_cache_title))
        builder.setMessage(getString(R.string.Clear_cache_message))

        builder.setPositiveButton(getString(R.string.Clear_cache)) { dialog: DialogInterface, _ ->
            FileUtils.clearCache()
            dialog.dismiss()
            finishAffinity()
        }

        builder.setNegativeButton(getString(R.string.NOClear_cache)) { dialog: DialogInterface, _ ->
            dialog.dismiss()
            finishAffinity()
        }

        val dialog: Dialog = builder.create()
        dialog.show()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 1001)
            }
        } else {
            val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
            val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

            if (ContextCompat.checkSelfPermission(this, readPermission) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, writePermission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(readPermission, writePermission), 1001)
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // 所有权限都被授予
            } else {
                // 至少有一个权限被拒绝
                checkPermission()
            }
        }
    }



    private fun showAgreementDialog(context: Context) {
        var dialogBinding: HomeDialogAgreementBinding? = HomeDialogAgreementBinding.inflate(LayoutInflater.from(this))

        val builder = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setView(dialogBinding?.root)
            .setTitle(R.string.Agreement_title)

        builder.setPositiveButton(getString(R.string.Agreement_ok)) { dialog: DialogInterface, _ ->
            SPUtils.putBoolean(agreement, true)
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.Agreement_no)) { dialog: DialogInterface, _ ->
            dialog.dismiss()
            finishAffinity()
        }

        val dialog = builder.create().apply {
            window?.let { dialogWindow ->
                setCanceledOnTouchOutside(false)
            }

            dialogBinding?.AgreementContent?.text = FileUtils.readFileFromAssets(
                LanguageHelper.getFileLanguage(
                    JsonConfigModifier.readJsonValue(
                        context,
                        jsonPath,
                        languageKey
                    ), context, "agreement", ""
                )
            )

            setOnDismissListener {
                dialogBinding = null
            }
        }

        dialog.show()
    }
}