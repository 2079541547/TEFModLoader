package silkways.terraria.toolbox.ui.fragment.main.manage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.MainFragmentManageBinding
import silkways.terraria.toolbox.logic.mod.ModJsonManager.extractAndMergeJsonFiles
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class ManageFragment: Fragment() {

    private var _binding: MainFragmentManageBinding? = null
    private val binding get() = _binding!!


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.manage)

        /*
        * @navHostFragment 获取导航控管理器
        * @navOptions 导航动画
        * navHostFragment.navController.navigate(R.id.页面id, null, navOptions)
        * 跳转方法
         */

        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_anim_enter)
            .setExitAnim(R.anim.fragment_anim_exit)
            .setPopEnterAnim(R.anim.fragment_anim_enter)
            .setPopExitAnim(R.anim.fragment_anim_exit)
            .build()

        _binding = MainFragmentManageBinding.inflate(inflater, container, false)



        binding.efmodManager.setOnClickListener {
            navHostFragment.navController.navigate(R.id.nanavigation_EFModManager, null, navOptions)
        }


        binding.installEfmod.setOnClickListener {
            selectModFiles()
        }




        return binding.root
    }

    private val selectFilesLauncher_mod = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            val efmodFilePaths = uris.mapNotNull { uri ->
                getRealPathFromURI(uri)?.let { path ->
                    File(path).absolutePath
                }
            }
            val extractToPath = "${requireActivity().getExternalFilesDir(null)}/ToolBoxData/ModData"

            if (efmodFilePaths.isNotEmpty()) {
                extractAndMergeJsonFiles(efmodFilePaths, extractToPath)
            } else {
                println("No valid files selected.")
            }
        }
    }


    private fun getRealPathFromURI(contentUri: Uri): String? {
        return requireActivity().contentResolver.openInputStream(contentUri)?.use { inputStream ->
            inputStream.readBytes().let { bytes ->
                val tempFile = File.createTempFile("temp", ".efmod")
                tempFile.writeBytes(bytes)
                tempFile.absolutePath
            }
        }
    }

    private fun selectModFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // 设置 MIME 类型为所有文件类型
        intent.type = "*/*"
        // 允许多个文件选择
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        selectFilesLauncher_mod.launch(intent.toString())
    }


    private fun copyFileOverwritingExisting(sourcePath: String?, destinationPath: String?) {
        val sourceFile = sourcePath?.let { File(it) }
        val destFile = destinationPath?.let { File(it) }

        // 删除文件
        if (destFile != null) {
            if (destFile.exists()) {
                if (destFile.delete()) {
                    Log.d("FileDeleted", "文件删除成功: ${destFile.absolutePath}")
                } else {
                    Log.e("FileDeleteError", "文件删除失败: ${destFile.absolutePath}")
                }
            } else {
                Log.i("FileNotFound", "文件不存在: ${destFile.absolutePath}")
            }
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


    private fun copyFileIfNotExists(sourcePath: String?, destinationPath: String?) {
        val sourceFile = sourcePath?.let { File(it) }
        val destFile = destinationPath?.let { File(it) }
        // 检查目标文件是否已经存在
        if (destFile != null) {
            if (destFile.exists()) {
                println("文件已存在")
                // 文件已存在，不做任何操作
                return
            }
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




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}