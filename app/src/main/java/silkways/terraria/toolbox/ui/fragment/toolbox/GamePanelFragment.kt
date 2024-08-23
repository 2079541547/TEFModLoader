package silkways.terraria.toolbox.ui.fragment.toolbox

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.toolbox.GameActivity
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.data.GameSettings
import silkways.terraria.toolbox.databinding.ToolboxFragmentGamepanelBinding
import silkways.terraria.toolbox.logic.JsonConfigModifier


/**
 * 关于页面的片段类，用于展示关于应用的信息。
 */
class GamePanelFragment: Fragment() {

    // 使用可空变量存储绑定对象，以便在销毁视图时能够释放资源
    private var _binding: ToolboxFragmentGamepanelBinding? = null
    // 提供非空的绑定访问方式
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.GamePanel)

        // 使用DataBindingUtil或ViewBinding inflate布局文件
        _binding = ToolboxFragmentGamepanelBinding.inflate(inflater, container, false)

        binding.StartGame.setOnClickListener {
            val intent = Intent(requireContext(), GameActivity::class.java)
            startActivity(intent)
        }


        binding.SuspendedWindow.isChecked = JsonConfigModifier.readJsonValue(requireActivity(), GameSettings.jsonPath, GameSettings.suspended_window) as Boolean
        binding.SuspendedWindow.setOnCheckedChangeListener{ _, isChecked ->
            JsonConfigModifier.modifyJsonConfig(requireActivity(), GameSettings.jsonPath, GameSettings.suspended_window, isChecked)
        }



        binding.debugGame.isChecked = JsonConfigModifier.readJsonValue(requireActivity(), GameSettings.jsonPath, GameSettings.debug) as Boolean
        binding.debugGame.setOnCheckedChangeListener{ _, isChecked ->
            JsonConfigModifier.modifyJsonConfig(requireActivity(), GameSettings.jsonPath, GameSettings.debug, isChecked)
        }




        return binding.root
    }




    /**
     * 当视图被销毁时调用，释放资源以避免内存泄漏。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // 清理绑定引用，帮助系统回收资源
        _binding = null
    }
}
