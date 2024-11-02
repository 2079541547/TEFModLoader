package silkways.terraria.efmodloader.ui.fragment.toolbox

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicator
import eternal.future.effsystem.fileSystem
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.GameSettings
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.data.TEFModLoader
import silkways.terraria.efmodloader.databinding.ToolboxFragmentGamepanelBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.mod.ModManager
import silkways.terraria.efmodloader.logic.modlaoder.LoaderManager
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

/**
 * GamePanelFragment 类表示一个用于启动游戏和调整游戏设置的界面片段。
 */
class GamePanelFragment : Fragment() {

    // 使用可空变量存储绑定对象，以便在销毁视图时能够安全地释放资源
    private var _binding: ToolboxFragmentGamepanelBinding? = null
    // 提供非空的绑定访问方式，用于在视图存在期间访问绑定对象
    private val binding get() = _binding!!


    @SuppressLint("RestrictedApi")
    private var loadingDialog: AlertDialog? = null

    /**
     * 创建视图。
     *
     * @param inflater 用于从布局文件创建视图的LayoutInflater对象。
     * @param container 如果非空，则此片段应附加到该容器。
     * @param savedInstanceState 保存的实例状态。
     * @return 返回创建的视图。
     */
    @SuppressLint("SdCardPath")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 设置顶部应用栏的标题
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.GamePanel)

        // 通过View Binding方式加载布局
        _binding = ToolboxFragmentGamepanelBinding.inflate(inflater, container, false)

        // 设置开始游戏按钮的点击监听器
        binding.StartGame.setOnClickListener {
            showLoadingDialog()

            // 模拟加载过程
            Thread {

                try {
                    when (JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.Runtime)) {
                        0 -> {
                            deleteDirectory(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFModX/"))
                            deleteDirectory(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFMod/"))

                            copyFilesFromToOgg(File(requireActivity().getExternalFilesDir(null), "EFMod-Private/"), File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/Private/"))
                        }

                        1 -> {
                            val a = JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.GamePackageName) as String
                            deleteDirectory(File("data/data/$a/cache/EFModX/"))
                            deleteDirectory(File("data/data/$a/cache/EFMod/"))
                            copyFilesFromTo(File(requireActivity().getExternalFilesDir(null), "EFMod-Private/"), File("/sdcard/Android/data/${JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.GamePackageName) as String}/files/EFMod-Private/"))
                        }
                    }
                } catch (e: IOException) {
                    Log.e("TEFModLoader", "错误：" , e)
                }

                LoaderManager.runEFModLoader(
                    "${requireActivity().getExternalFilesDir(null)}/ToolBoxData/EFModLoaderData/info.json",
                    requireActivity())

                ModManager.runEFMod(
                    "${requireActivity().getExternalFilesDir(null)}/ToolBoxData/EFModData/info.json",
                    requireActivity())


                try {
                    if (JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.Runtime) == 0) {
                        renameFilesWithOggExtension(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFModX"))
                        renameFilesWithOggExtension(File("/sdcard/Documents/EFModLoader/${TEFModLoader.TAG}/EFMod"))
                    }
                } catch (e: IOException) {
                    Log.e("TEFModLoader", "错误：" , e)
                }

                dismissLoadingDialog()

                launchApp(JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.GamePackageName) as String)

            }.start()
        }

        // 初始化悬浮窗口复选框的状态
        binding.SuspendedWindow.isChecked = JsonConfigModifier.readJsonValue(
            requireActivity(),
            GameSettings.jsonPath,
            GameSettings.suspended_window
        ) as Boolean

        // 设置悬浮窗口复选框的改变监听器
        binding.SuspendedWindow.setOnCheckedChangeListener { _, isChecked ->
            // 修改JSON配置中的悬浮窗口设置
            JsonConfigModifier.modifyJsonConfig(
                requireActivity(),
                GameSettings.jsonPath,
                GameSettings.suspended_window,
                isChecked
            )
        }

        // 初始化调试模式复选框的状态
        binding.debugGame.isChecked = JsonConfigModifier.readJsonValue(
            requireActivity(),
            GameSettings.jsonPath,
            GameSettings.debug
        ) as Boolean

        // 设置调试模式复选框的改变监听器
        binding.debugGame.setOnCheckedChangeListener { _, isChecked ->
            // 修改JSON配置中的调试模式设置
            JsonConfigModifier.modifyJsonConfig(
                requireActivity(),
                GameSettings.jsonPath,
                GameSettings.debug,
                isChecked
            )
        }

        // 返回绑定对象的根视图
        return binding.root
    }


    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val circularProgressIndicator = CircularProgressIndicator(requireActivity()).apply {
                setIndeterminate(true)
                setPadding(15, 15, 15, 15)
            }

            loadingDialog = MaterialAlertDialogBuilder(requireActivity())
                .setTitle(requireActivity().getString(R.string.loadingMod))
                .setView(circularProgressIndicator)
                .setCancelable(true)
                .create()

            // 确保加载框不会因为点击外部区域而消失
            loadingDialog?.setCanceledOnTouchOutside(false)
        }
        loadingDialog?.show()
    }

    private fun dismissLoadingDialog() {
        loadingDialog?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }


    private fun launchApp(packageName: String) {
        try {
            // 获取目标应用的启动Intent
            val intent = requireActivity().packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                // 如果找到目标应用的启动Intent，启动该应用
                startActivity(intent)
            } else {
                Log.e("TEFModLoader", "目标应用未安装：")
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("TEFModLoader", "错误：" , e)
        }
    }

    private fun renameFilesWithOggExtension(directory: File) {
        if (!directory.exists() || !directory.isDirectory) {
            println("指定的路径不是一个有效的目录: ${directory.absolutePath}")
            return
        }

        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                // 递归处理子目录
                renameFilesWithOggExtension(file)
            } else {
                // 重命名文件，添加 .ogg 扩展名
                val newFileName = "${file.name}.ogg"
                val newFilePath = File(file.parent, newFileName)
                if (file.renameTo(newFilePath)) {
                    println("文件重命名成功: ${file.name} -> $newFileName")
                } else {
                    println("文件重命名失败: ${file.name}")
                }
            }
        }
    }


    fun copyFilesFromTo(sourceDir: File, destDir: File) {
        if (!sourceDir.exists()) {
            println("源目录不存在: ${sourceDir.absolutePath}")
            return
        }

        if (!destDir.exists()) {
            destDir.mkdirs()
            println("创建目标目录: ${destDir.absolutePath}")
        }

        sourceDir.listFiles()?.forEach { entry ->
            val sourcePath = entry
            val destPath = File(destDir, sourcePath.name)

            if (entry.isDirectory) {
                // 递归复制子目录
                copyFilesFromTo(entry, destPath)
            } else {
                // 复制文件并设置时间戳
                copyFileWithTimestamp(sourcePath, destPath)
            }
        }
    }


    fun copyFilesFromToOgg(sourceDir: File, destDir: File) {
        if (!sourceDir.exists()) {
            println("源目录不存在: ${sourceDir.absolutePath}")
            return
        }

        if (!destDir.exists()) {
            destDir.mkdirs()
            println("创建目标目录: ${destDir.absolutePath}")
        }

        sourceDir.listFiles()?.forEach { entry ->
            val sourcePath = entry
            val destPath: File

            if (entry.isDirectory) {
                // 递归复制子目录
                destPath = File(destDir, entry.name)
                copyFilesFromToOgg(entry, destPath)
            } else {
                // 在文件名后面添加 .ogg 后缀
                destPath = File(destDir, "${entry.name}.ogg")
                // 复制文件并设置时间戳
                copyFileWithTimestamp(sourcePath, destPath)
            }
        }
    }



    @Throws(IOException::class)
    fun copyFileWithTimestamp(sourcePath: File, destPath: File) {
        if (destPath.exists()) {
            // 比较时间戳和文件大小
            val sourceLastWriteTime = sourcePath.lastModified()
            val destLastWriteTime = destPath.lastModified()
            val sourceFileSize = sourcePath.length()
            val destFileSize = destPath.length()

            if (sourceLastWriteTime != destLastWriteTime || sourceFileSize != destFileSize) {
                // 文件不同，执行复制
                try {
                    copyFile(sourcePath, destPath)
                    setLastModifiedTime(destPath, sourceLastWriteTime)
                    println("复制文件: ${sourcePath.absolutePath} 到 ${destPath.absolutePath}")
                } catch (e: IOException) {
                    println("复制文件失败: ${sourcePath.absolutePath} 错误: ${e.message}")
                }
            } else {
                println("文件相同，跳过复制: ${sourcePath.absolutePath}")
            }
        } else {
            try {
                copyFile(sourcePath, destPath)
                setLastModifiedTime(destPath, sourcePath.lastModified())
                println("复制文件: ${sourcePath.absolutePath} 到 ${destPath.absolutePath}")
            } catch (e: IOException) {
                println("复制文件失败: ${sourcePath.absolutePath} 错误: ${e.message}")
            }
        }
    }

    @Throws(IOException::class)
    fun copyFile(source: File, target: File) {
        FileInputStream(source).use { input ->
            FileOutputStream(target).use { output ->
                val channelIn: FileChannel = input.channel
                val channelOut: FileChannel = output.channel
                channelIn.transferTo(0, channelIn.size(), channelOut)
            }
        }
    }

    fun setLastModifiedTime(file: File, lastModifiedTime: Long) {
        file.setLastModified(lastModifiedTime)
    }


    private fun deleteDirectory(directory: File) {
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            }
            directory.delete()
        }
    }

    /**
     * 当视图被销毁时调用。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // 清除绑定引用，防止内存泄漏
        _binding = null
    }
}