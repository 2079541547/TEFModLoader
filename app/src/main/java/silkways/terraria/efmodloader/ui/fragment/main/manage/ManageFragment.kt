package silkways.terraria.efmodloader.ui.fragment.main.manage

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.MainFragmentManageBinding
import silkways.terraria.efmodloader.logic.mod.ModInstaller
import silkways.terraria.efmodloader.logic.modlaoder.modLoaderInstaller
import silkways.terraria.efmodloader.ui.fragment.main.toolbox.logic.FileItem
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
            selectFilesLauncher.launch("*/*")
        }

        binding.installRes.setOnClickListener {
            Snackbar.make(it, "真的在写辣QAQ", Snackbar.LENGTH_SHORT).show()
        }

        binding.resManager.setOnClickListener {
            Snackbar.make(it, "真的再写了QWQ", Snackbar.LENGTH_SHORT).show()
        }


        binding.InstallKernel.setOnClickListener {
            selectFilesLauncher_k.launch("*/*")
        }

        binding.KernelManagement.setOnClickListener {
            navHostFragment.navController.navigate(R.id.nanavigation_EFModLoaderManager, null, navOptions)
        }


        return binding.root
    }

    private fun loadFiles(directory: File): List<FileItem> {
        val items = mutableListOf<FileItem>()

        val files = directory.listFiles { _, _ -> true } ?: return items
        files.forEach { file ->
            if (file.isDirectory) {
                items.addAll(loadFiles(file))
            } else {
                items.add(FileItem(file.name, false, file.absolutePath))
            }
        }

        return items
    }




    private val selectFilesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            val efmodFilePaths = uris.mapNotNull { uri ->
                getRealPathFromURI(uri)?.let { path ->
                    File(path).absolutePath
                }
            }

            if (efmodFilePaths.isNotEmpty()) {
                efmodFilePaths.forEach { filePath ->

                    val file = File(filePath)
                    val file2 = file.name
                    val file1 = file.parent

                    val rootDirectory ="${requireActivity().getExternalFilesDir(null)?.absolutePath}/ToolBoxData/EFModData"

                    val destinationPath = "$rootDirectory/"

                    if (file1 != null) {
                        ModInstaller.moveFileAndUpdateConfig(requireActivity(), file1, destinationPath, file2)
                    }

                }
            } else {
                println("No valid files selected.")
            }
        }
    }

    private val selectFilesLauncher_k = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (uris.isNotEmpty()) {
            val efmodFilePaths = uris.mapNotNull { uri ->
                getRealPathFromURI(uri)?.let { path ->
                    File(path).absolutePath
                }
            }

            if (efmodFilePaths.isNotEmpty()) {
                efmodFilePaths.forEach { filePath ->

                    val file = File(filePath)
                    val file2 = file.name
                    val file1 = file.parent

                    val rootDirectory ="${requireActivity().getExternalFilesDir(null)?.absolutePath}/ToolBoxData/EFModLoaderData"

                    val destinationPath = "$rootDirectory/"

                    if (file1 != null) {
                        modLoaderInstaller.moveFileAndUpdateConfig(requireActivity(), file1, destinationPath, file2)
                    }

                }
            } else {
                println("No valid files selected.")
            }
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        return requireActivity().contentResolver.openInputStream(contentUri)?.use { inputStream ->
            File(requireActivity().cacheDir.toString()).mkdirs()
            val fileName = getFileNameFromURI(contentUri)
            val tempDir = File.createTempFile("temp", "").parentFile // 获取临时目录
            val tempFile = File(tempDir, fileName) // 使用原始文件名创建新文件
            tempFile.writeBytes(inputStream.readBytes())
            tempFile.absolutePath
        }
    }

    // 辅助函数用于从 Uri 获取文件名
    private fun getFileNameFromURI(uri: Uri): String {
        return requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.getString(nameIndex)
        } ?: ""
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