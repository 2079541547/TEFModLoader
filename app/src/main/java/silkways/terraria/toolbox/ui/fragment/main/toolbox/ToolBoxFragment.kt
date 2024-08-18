package silkways.terraria.toolbox.ui.fragment.main.toolbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.MainFragmentToolboxBinding
import silkways.terraria.toolbox.ui.fragment.main.toolbox.logic.FileItem
import silkways.terraria.toolbox.ui.fragment.main.toolbox.logic.FileListAdapter
import java.io.File


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
            Toast.makeText(context, "骗你的，我根本没写", Toast.LENGTH_SHORT).show()
        }

        binding.ImportConfiguration.setOnClickListener {
            Toast.makeText(context, "骗你的，我根本没写", Toast.LENGTH_SHORT).show()
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}