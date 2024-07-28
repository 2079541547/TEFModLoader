package silkways.terraria.toolbox.ui.fragment.home.about

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.HomeFragmentAboutBinding

/**
 * 关于页面的片段类，用于展示关于应用的信息。
 */
class AboutFragment: Fragment() {

    // 使用可空变量存储绑定对象，以便在销毁视图时能够释放资源
    private var _binding: HomeFragmentAboutBinding? = null
    // 提供非空的绑定访问方式
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 设置ActionBar的标题
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = getString(R.string.about)

        // 使用DataBindingUtil或ViewBinding inflate布局文件
        _binding = HomeFragmentAboutBinding.inflate(inflater, container, false)



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

class WebAppInterface(private val context: Context) {
    @JavascriptInterface
    fun applyLanguage(language: String) {
        // 这里实际上不需要做任何事情，因为我们只是通过这个接口传递语言到JavaScript
    }
}