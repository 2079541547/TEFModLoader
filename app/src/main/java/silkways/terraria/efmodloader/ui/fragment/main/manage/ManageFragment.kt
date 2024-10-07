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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.MainFragmentManageBinding
import silkways.terraria.efmodloader.logic.mod.ModInstaller
import silkways.terraria.efmodloader.ui.fragment.main.toolbox.logic.FileItem
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class ManageFragment: Fragment() {

    private var _binding: MainFragmentManageBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectFilesLauncher: ActivityResultLauncher<Intent>

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

        selectFilesLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result != null && result.data != null) {
                val uris = result.data?.clipData?.uriList()
                    ?: listOf(result.data?.data!!)
                uris.forEach { uri ->
                    handleFileUri(uri)
                }
            }
        }

        binding.installEfmod.setOnClickListener {
            selectModFiles()
        }

        binding.installRes.setOnClickListener {
            Snackbar.make(it, "真的在写辣QAQ", Snackbar.LENGTH_SHORT).show()
        }

        binding.resManager.setOnClickListener {
            Snackbar.make(it, "真的再写了QWQ", Snackbar.LENGTH_SHORT).show()
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



    private fun selectModFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*" // 设置 MIME 类型为所有文件类型
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 允许多个文件选择
        selectFilesLauncher.launch(intent)
    }

    private fun handleFileUri(uri: Uri) {
        // 处理每个文件 URI
        val filePath = getRealPathFromURI(uri)
        filePath?.let { path ->
            // 进行文件处理操作
            val file = File(path)
            val fileName = file.name
            val fileParent = file.parent

            val rootDirectory = "${requireContext().getExternalFilesDirs(null)}/ToolBoxData/EFModData"
            val destinationPath = "$rootDirectory/"

            if (fileParent != null) {
                // 假设 ModInstaller 是一个自定义类，用于移动文件并更新配置
                ModInstaller.moveFileAndUpdateConfig(requireContext(), fileParent, destinationPath, fileName)
            } else {
                Log.e("ManageFragment", "File parent is null")
            }
        } ?: run {
            Log.e("ManageFragment", "Failed to get file path from URI")
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        return try {
            val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
            requireActivity().contentResolver.query(contentUri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                    cursor.getString(columnIndex)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }.toString()
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

