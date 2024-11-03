package silkways.terraria.efmodloader.ui.fragment.main.manage

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.*
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.MainFragmentManageBinding
import silkways.terraria.efmodloader.logic.efmod.LoaderManager
import silkways.terraria.efmodloader.logic.efmod.ModManager
import silkways.terraria.efmodloader.utils.FileUtils
import silkways.terraria.efmodloader.logic.EFLog
import silkways.terraria.efmodloader.ui.activity.ManageActivity
import java.io.File

class ManageFragment : Fragment() {

    private var _binding: MainFragmentManageBinding? = null
    private val binding get() = _binding!!

    // 启动选择文件的活动结果
    private val selectModsLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        handleSelectedFiles(uris, ModManager::install, "TEFModLoader/EFModData")
    }

    private val selectLoadersLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        handleSelectedFiles(uris, LoaderManager::install, "TEFModLoader/EFModLoaderData")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 设置顶部应用栏标题
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).title = getString(R.string.manage)
        _binding = MainFragmentManageBinding.inflate(inflater, container, false)

        // 绑定安装模组按钮点击事件
        binding.installEfmod.setOnClickListener {
            selectModsLauncher.launch("*/*")
        }

        // 绑定安装内核按钮点击事件
        binding.InstallKernel.setOnClickListener {
            selectLoadersLauncher.launch("*/*")
        }

        binding.efmodManager.setOnClickListener {
            val intent = Intent(requireActivity(), ManageActivity::class.java)
            intent.putExtra("Title", getString(R.string.efmod_manager))
            intent.putExtra("isMod", true)
            startActivity(intent)
        }

        binding.KernelManagement.setOnClickListener {
            val intent = Intent(requireActivity(), ManageActivity::class.java)
            intent.putExtra("Title", getString(R.string.Kernel_management))
            intent.putExtra("isMod", false)
            startActivity(intent)
        }

        return binding.root
    }

    private fun handleSelectedFiles(
        uris: List<Uri>,
        installAction: (context: Context, file: File, destination: File) -> Unit,
        destPath: String
    ) {
        if (uris.isEmpty()) {
            EFLog.w("没有选择任何文件。")
            return
        }

        // 获取目标目录路径
        val rootDirectory = "${requireActivity().getExternalFilesDir(null)?.absolutePath}/$destPath"
        val destination = File(rootDirectory)

        // 开始协程处理每个选中的文件
        CoroutineScope(Dispatchers.IO).launch {
            uris.forEach { uri ->
                val filePath = FileUtils.getRealPathFromURI(uri, requireActivity())
                filePath?.let {
                    val file = File(it)
                    EFLog.i("开始安装文件：${file.name}")
                    withContext(Dispatchers.Main) {
                        try {
                            installAction(requireActivity(), file, destination)
                            EFLog.i("文件安装成功：${file.name}")
                        } catch (e: Exception) {
                            EFLog.e("文件安装失败：${file.name}，原因：${e.message}")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}