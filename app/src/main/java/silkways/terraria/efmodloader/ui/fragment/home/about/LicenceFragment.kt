package silkways.terraria.efmodloader.ui.fragment.home.about

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.HomeAboutLicenceBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.Markdown


/**
 * 关于页面的片段类，用于展示关于应用的信息。
 */
class LicenceFragment: Fragment() {

    // 使用可空变量存储绑定对象，以便在销毁视图时能够释放资源
    private var _binding: HomeAboutLicenceBinding? = null
    // 提供非空的绑定访问方式
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.Open_source_license)

        // 使用DataBindingUtil或ViewBinding inflate布局文件
        _binding = HomeAboutLicenceBinding.inflate(inflater, container, false)

        val webView = binding.Licence
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.clearCache(true)

        val markdownContent = Markdown.loadMarkdownFromAssets(requireActivity(), LanguageHelper.getMDLanguage(
            JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.languageKey),
            requireActivity(),
            "Home/About/Licence"),
        )

        val htmlContent = Markdown.markdownToHtml(markdownContent, JsonConfigModifier.readJsonValue(requireActivity(), Settings.jsonPath, Settings.themeKey))
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)

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
