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
import com.google.android.material.snackbar.Snackbar
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.MainFragmentManageBinding
import silkways.terraria.toolbox.logic.ApkPatcher
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


        fun getFileSize(file: File): Long {
            return if (file.exists()) {
                file.length()
            } else {
                0L
            }
        }

        fun getFileSizes(file1: File, file2: File): Pair<String, String> {
            val size1 = getFileSize(file1)
            val size2 = getFileSize(file2)

            val result1 = "${file1.absolutePath}: $size1 byte"
            val result2 = "${file2.absolutePath}: $size2 byte"

            return Pair(result1, result2)
        }


        binding.materialCardView.setOnLongClickListener {
            val file = File("${requireActivity().cacheDir}/lspatch/origin/")
            val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
            copyFileIfNotExists("${requireActivity().cacheDir}/lspatch/origin/${files?.get(0)?.name}", "${requireActivity().getExternalFilesDir(null)}/ToolBoxData/APK/base.apk")

            Snackbar.make(it, getString(R.string.Copy_successful), Snackbar.LENGTH_SHORT).show()
            true
        }

        /*
        if(true){
            val (result1, result2) = getFileSizes(File("${requireActivity().cacheDir}/lspatch/origin/${files?.get(0)?.name}"), File("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/APK/base.apk"))
            binding.textView6.text = result1 + "\n" + result2
        }
         */


        binding.UpdateAPK.setOnClickListener {
            val file = File("${requireActivity().cacheDir}/lspatch/origin/")
            val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
            copyFileOverwritingExisting("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/APK/base.apk", "${requireActivity().cacheDir}/lspatch/origin/${files?.get(0)?.name}")

            Snackbar.make(it, getString(R.string.UpdateAPK), Snackbar.LENGTH_SHORT).show()
        }

        binding.UpdateAPK.setOnLongClickListener {
            ApkPatcher.addSOsToAPK("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/APK/base.apk", "${requireActivity().getExternalFilesDir(null)}/ToolBoxData/ModData")
            Snackbar.make(it, getString(R.string.APK_repair), Snackbar.LENGTH_SHORT).show()
            true
        }


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
                ApkPatcher.addSOsToAPK("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/APK/base.apk", extractToPath)

                //val file = File("${requireActivity().cacheDir}/lspatch/origin/")
                //val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
                //copyFileOverwritingExisting("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/APK/base.apk", "${requireActivity().cacheDir}/lspatch/origin/${files?.get(0)?.name}")
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


    fun copyFileOverwritingExisting(sourcePath: String?, destinationPath: String?) {
        val sourceFile = File(sourcePath)
        val destFile = File(destinationPath)

        // 删除文件
        if (destFile.exists()) {
            if (destFile.delete()) {
                Log.d("FileDeleted", "文件删除成功: ${destFile.absolutePath}")
            } else {
                Log.e("FileDeleteError", "文件删除失败: ${destFile.absolutePath}")
            }
        } else {
            Log.i("FileNotFound", "文件不存在: ${destFile.absolutePath}")
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




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}