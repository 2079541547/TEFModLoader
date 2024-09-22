package silkways.terraria.efmodloader.ui.fragment.toolbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import org.json.JSONObject
import silkways.terraria.efmodloader.GameActivity
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.GameSettings
import silkways.terraria.efmodloader.databinding.ToolboxFragmentGamepanelBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.mod.ModManager
import java.io.File

/**
 * GamePanelFragment 类表示一个用于启动游戏和调整游戏设置的界面片段。
 */
class GamePanelFragment : Fragment() {

    // 使用可空变量存储绑定对象，以便在销毁视图时能够安全地释放资源
    private var _binding: ToolboxFragmentGamepanelBinding? = null
    // 提供非空的绑定访问方式，用于在视图存在期间访问绑定对象
    private val binding get() = _binding!!

    /**
     * 创建视图。
     *
     * @param inflater 用于从布局文件创建视图的LayoutInflater对象。
     * @param container 如果非空，则此片段应附加到该容器。
     * @param savedInstanceState 保存的实例状态。
     * @return 返回创建的视图。
     */
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

            initialization()

            // 创建意图来启动游戏活动
            val intent = Intent(requireContext(), GameActivity::class.java)
            // 启动游戏活动
            startActivity(intent)

            requireActivity().finish()
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



    private fun initialization() {
        val filePath = "${requireActivity().getExternalFilesDir(null)}/ToolBoxData/EFModData/info.json"
        val file = File(filePath)

        if (file.exists()) {
            val bufferedReader = file.bufferedReader()
            val jsonString = bufferedReader.use { it.readText() }

            // 解析JSON
            val jsonObject = JSONObject(jsonString)

            // 遍历JSON对象
            val iterator = jsonObject.keys()
            while (iterator.hasNext()) {
                val key = iterator.next()
                if (jsonObject.getBoolean(key)) {
                    ModManager.enableEFMod(requireActivity(), key)
                    Log.d("GamePanelFragment", "Key: $key") // 如果值为true，打印键
                }
            }
        } else {
            Log.e("GamePanelFragment", "File not found at path: $filePath")
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