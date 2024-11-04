package silkways.terraria.efmodloader.ui.fragment.main.toolbox

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.FragmentMainToolboxBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.efmod.Init
import silkways.terraria.efmodloader.ui.activity.TerminalActivity
import silkways.terraria.efmodloader.ui.adapter.FileItem
import silkways.terraria.efmodloader.ui.adapter.FileListAdapter
import silkways.terraria.efmodloader.utils.FileUtils
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ToolBoxFragment: Fragment() {

    private var _binding: FragmentMainToolboxBinding? = null
    private val binding get() = _binding!!

    private lateinit var createFileLauncher: androidx.activity.result.ActivityResultLauncher<String>
    private lateinit var createFileLauncher_1: androidx.activity.result.ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActivityResultLauncher(listOf("${requireActivity().getExternalFilesDir(null)?.parent}/Worlds", "${requireActivity().getExternalFilesDir(null)?.parent}/Players", "${requireActivity().getExternalFilesDir(null)?.parent}/OldSaves"))
        setupActivityResultLauncher_1()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.toolbox)

        _binding = FragmentMainToolboxBinding.inflate(inflater, container, false)

        binding.ImportArchive.setOnClickListener {
            selectModFiles()
        }

        binding.ImportConfiguration.setOnClickListener {
            selectModFiles()
        }

        binding.dumpSave.setOnClickListener {
            createFileLauncher.launch("存档.zip")
        }

        binding.dumpAll.setOnClickListener {
            createFileLauncher_1.launch("TEFModLoader数据")
        }

        binding.gamePanel.setOnClickListener {
            Init(requireActivity()).initialization()
        }

        binding.terminal.setOnClickListener {
            val intent = Intent(requireActivity(), TerminalActivity::class.java)
            startActivity(intent)
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
                FileUtils.getRealPathFromURI(uri, requireActivity())?.let { path ->
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
                FileUtils.copyFile(file.absolutePath, destinationFile.absolutePath, true)
            } else {
                CoveringFiles_dialog(file, destinationPath)
            }
        } else {
            // 文件不存在，复制文件
            FileUtils.copyFile(file.absolutePath, destinationFile.absolutePath, true)
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
            FileUtils.copyFile(file.absolutePath, destinationFile.absolutePath, true)
            dialog.dismiss()
        }

        builder.setNegativeButton(getString(R.string.close)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }

        val dialog: Dialog = builder.create()
        dialog.show()

    }

    private fun selectModFiles() {
        selectFilesLauncher.launch("*/*")
    }


    private fun setupActivityResultLauncher(dirPaths: List<String>) {
        createFileLauncher = registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/zip")
        ) { uri ->
            uri?.let {
                saveZipToFile(it, dirPaths)
            }
        }
    }

    private fun setupActivityResultLauncher_1() {
        createFileLauncher_1 = registerForActivityResult(
            ActivityResultContracts.CreateDocument("application/zip")
        ) { uri ->
            uri?.let {
                saveZipToFile(it, listOf(), true)
            }
        }
    }

    private fun saveZipToFile(uri: Uri, dirPaths: List<String> = listOf<String>(), isAll: Boolean = false) {
        try {
            val fileOutputStream = requireActivity().contentResolver.openOutputStream(uri)
            fileOutputStream?.use { outputStream ->
                val zipOut = ZipOutputStream(outputStream)

                if (isAll) {
                    val dir = File("${requireActivity().getExternalFilesDir(null)?.parent}")
                    val dir2 = File("${requireActivity().dataDir}")

                    compressDir(dir, "sdcard/", zipOut)
                    compressDir(dir2, "data/", zipOut)
                }
                else {
                    for (dirPath in dirPaths) {
                        val dir = File(dirPath)
                        if (dir.exists()) {
                            compressDir(dir, dir.name + "/", zipOut)
                        }

                    }
                }
                zipOut.close()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saving ZIP file", e)
        }
    }

    private fun compressDir(dir: File, parentPath: String, zipOut: ZipOutputStream) {
        val files = dir.listFiles()
        if (files != null) {
            for (file in files) {
                try {
                    if (file.isDirectory) {
                        compressDir(file, parentPath + file.name + "/", zipOut)
                    } else {
                        val entryName = parentPath + file.name
                        zipOut.putNextEntry(ZipEntry(entryName))
                        val buffer = ByteArray(1024)
                        var length: Int
                        val fis = FileInputStream(file)
                        while (fis.read(buffer).also { length = it } > 0) {
                            zipOut.write(buffer, 0, length)
                        }
                        zipOut.closeEntry()
                        fis.close()
                    }
                } catch (e: Exception) {
                    // Log the exception and continue to the next file
                    println("Failed to compress file: ${file.absolutePath}. Reason: ${e.message}")
                    continue
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}