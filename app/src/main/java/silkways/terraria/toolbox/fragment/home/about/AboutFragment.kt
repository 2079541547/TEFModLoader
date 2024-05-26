package silkways.terraria.toolbox.fragment.home.about

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    /**
     * 创建视图。
     * 此方法负责初始化并配置关于页面的UI组件。
     *
     * @param inflater 布局填充器，用于将XML布局转换为视图对象。
     * @param container 视图容器，如果存在，则在此容器中添加新创建的视图。
     * @param savedInstanceState 保存的实例状态，用于恢复片段状态。
     * @return 返回关于页面的根视图。
     */
    @SuppressLint("SetJavaScriptEnabled") // 忽略开启JavaScript的安全警告，因为这是本地HTML文件
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

        // 获取WebView的设置对象
        val webViewSettings = binding.webView.settings

        // 启用JavaScript支持，以便可以执行HTML中的JavaScript代码
        webViewSettings.javaScriptEnabled = true

        // 设置WebViewClient，确保所有页面都在当前WebView中加载，而不是启动外部浏览器
        binding.webView.webViewClient = WebViewClient()

        // 加载assets文件夹内的HTML文件作为内容
        binding.webView.loadUrl("file:///android_asset/web/about/main.html")

        // 返回绑定的根视图，即关于页面的视图
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
