package silkways.terraria.efmodloader.ui.fragment.main.toolbox

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.MainFragmentToolboxBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.ui.fragment.main.toolbox.logic.FileItem
import silkways.terraria.efmodloader.ui.fragment.main.toolbox.logic.FileListAdapter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class ToolBoxFragment: Fragment() {

    private var _binding: MainFragmentToolboxBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.toolbox)

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

        _binding = MainFragmentToolboxBinding.inflate(inflater, container, false)



        binding.gamePanel.setOnClickListener {
            navHostFragment.navController.navigate(R.id.nanavigation_GamePanel, null, navOptions)
        }

        binding.terminal.setOnClickListener {
            navHostFragment.navController.navigate(R.id.navigation_terminal, null, navOptions)
        }


        binding.ImportArchive.setOnClickListener {
            selectModFiles()
        }

        binding.ImportConfiguration.setOnClickListener {
            selectModFiles()
        }


        //初始化文件管理
        val recyclerView = binding.fileList
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        val rootDirectory = requireActivity().getExternalFilesDir(null)?.absolutePath?.let { File(it).parent }
        val files = rootDirectory?.let { File(it) }?.let { loadFiles(it) }
        val fileListAdapter = files?.let { FileListAdapter(it, requireActivity()) }
        recyclerView.adapter = fileListAdapter


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
                    val extension = file.extension

                    val rootDirectory = requireActivity().getExternalFilesDir(null)?.absolutePath?.let { File(it).parent }

                    val destinationPlyPath = "$rootDirectory/Players/"
                    val destinationWldPath = "$rootDirectory/Worlds/"
                    val destinationPath = "$rootDirectory/"

                    when (extension) {
                        "plr" -> {
                            // 复制 .plr 文件
                            handleFile(file, destinationPlyPath, "PLR")
                        }
                        "wld" -> {
                            // 复制 .wld 文件
                            handleFile(file, destinationWldPath, "WLD")
                        }
                        else -> {
                            // 复制其他类型的文件
                            handleFile(file, destinationPath, "OTHER")
                        }
                    }
                }
            } else {
                println("No valid files selected.")
            }
        }
    }



    private fun getRealPathFromURI(contentUri: Uri): String? {
        return requireActivity().contentResolver.openInputStream(contentUri)?.use { inputStream ->
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

    private fun handleFile(file: File, destinationPath: String, fileType: String) {
        val fileName = file.name
        val destinationFile = File(destinationPath, fileName)

        // 创建目录（如果不存在）
        val destinationDirectory = destinationFile.parentFile
        if (destinationDirectory != null) {
            if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) {
                println("Failed to create directory: ${destinationDirectory.absolutePath}")
                return
            }
        }

        if (destinationFile.exists()) {
            // 文件已存在，执行其他操作
            println("File already exists: ${file.name}. Skipping copy.")

            if(JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.CoveringFiles) as Boolean){
                copyFileOverwritingExisting(file.absolutePath, destinationFile.absolutePath)
            } else {
                CoveringFiles_dialog(file, destinationPath)
            }
        } else {
            // 文件不存在，复制文件
            copyFileOverwritingExisting(file.absolutePath, destinationFile.absolutePath)
            println("Copied $fileType file: ${file.name} to $destinationPath")
        }
    }


    private fun CoveringFiles_dialog(file: File, destinationPath: String){

        val fileName = file.name
        val destinationFile = File(destinationPath, fileName)


        val builder = MaterialAlertDialogBuilder(requireActivity())
            .setCancelable(false)

        builder.setTitle(getString(R.string.CoveringFiles_dialog_title))
        builder.setMessage(getString(R.string.CoveringFiles_dialog_message))

        builder.setPositiveButton(getString(R.string.determine)) { dialog: DialogInterface, _: Int ->
            copyFileOverwritingExisting(file.absolutePath, destinationFile.absolutePath)
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.close)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        val dialog: Dialog = builder.create()
        dialog.show()

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
            if (destFile != null) {
                FileInputStream(sourceFile).use { fis ->
                    FileOutputStream(destFile).use { fos ->
                        fis.channel.use { inputChannel ->
                            fos.channel.use { outputChannel ->
                                // 直接使用FileChannel进行高效复制
                                inputChannel.transferTo(0, inputChannel.size(), outputChannel)
                                Log.i("FileNotFound", "复制成功: ${destFile.absolutePath}")
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    private fun selectModFiles() {
        selectFilesLauncher.launch("*/*")
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}