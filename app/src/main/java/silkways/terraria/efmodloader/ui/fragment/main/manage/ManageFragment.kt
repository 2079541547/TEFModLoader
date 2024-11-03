package silkways.terraria.efmodloader.ui.fragment.main.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.*
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.MainFragmentManageBinding
import silkways.terraria.efmodloader.logic.efmod.ModManager
import java.io.File

class ManageFragment : Fragment() {

    private var _binding: MainFragmentManageBinding? = null
    private val binding get() = _binding!!

    private lateinit var selectFilesLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).title = getString(R.string.manage)
        _binding = MainFragmentManageBinding.inflate(inflater, container, false)

        binding.installEfmod.setOnClickListener { selectAndInstallFiles() }
        binding.InstallKernel.setOnClickListener { selectAndInstallFiles() }

        selectFilesLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            handleSelectedFiles(uris) { file ->
                // 处理文件安装
                installFile(file, ModManager::install)
            }
        }

        return binding.root
    }

    private fun selectAndInstallFiles() {
        selectFilesLauncher.launch("*/*")
    }

    private fun handleSelectedFiles(uris: List<Uri>, onFileReady: (File) -> Unit) {
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                getRealPathFromURI(uri)?.let { path ->
                    val file = File(path)
                    onFileReady(file)
                }
            }
        } else {
            println("No valid files selected.")
        }
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        return requireActivity().contentResolver.openInputStream(contentUri)?.use { inputStream ->
            val fileName = getFileNameFromURI(contentUri)
            val tempFile = File(requireActivity().cacheDir, fileName)
            tempFile.writeBytes(inputStream.readBytes())
            tempFile.absolutePath
        }
    }

    @SuppressLint("Range")
    private fun getFileNameFromURI(uri: Uri): String {
        return requireActivity().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
        } ?: ""
    }

    private fun installFile(file: File, installer: (activity: Activity, file: File, destination: File) -> Unit) {
        val destinationDirectory = getDestinationDirectory()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 拷贝文件到目标目录
                val destinationFile = File(destinationDirectory, file.name)
                file.copyTo(destinationFile, overwrite = true)
                // 调用安装逻辑
                withContext(Dispatchers.Main) {
                    installer(requireActivity(), destinationFile, destinationDirectory)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // 处理异常，例如显示错误信息
                    println("Error installing file: ${e.message}")
                }
            }
        }
    }

    private fun getDestinationDirectory(): File {
        return File("${requireActivity().getExternalFilesDir(null)?.absolutePath}/TEFModLoader/EFModData")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}