package silkways.terraria.efmodloader.ui.fragment.manage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.ManageEfmodWebBinding
import java.io.File

/**
 * 关于页面的片段类，用于展示关于应用的信息。
 */
class EFModWebFragment: Fragment() {

    // 使用可空变量存储绑定对象，以便在销毁视图时能够释放资源
    private var _binding: ManageEfmodWebBinding? = null
    // 提供非空的绑定访问方式
    private val binding get() = _binding!!

    private var modCacheDir = ""
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val title = arguments?.getString("Title")
        val url = arguments?.getString("Url")
        modCacheDir = arguments?.getString("modCacheDir").toString()

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(title)

        // 使用DataBindingUtil或ViewBinding inflate布局文件
        _binding = ManageEfmodWebBinding.inflate(inflater, container, false)

        webView = binding.EFModPage
        val webSettings = webView?.settings
        webSettings?.javaScriptEnabled = true
        webSettings?.useWideViewPort = true
        webSettings?.loadWithOverviewMode = true
        webSettings?.cacheMode = WebSettings.LOAD_NO_CACHE
        webView?.clearCache(true)
        webView?.settings?.javaScriptEnabled = true
        webView?.webViewClient = object : WebViewClient() {
            @SuppressLint("ClickableViewAccessibility")
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val jsCode = "PrivateDirectory = '${arguments?.getString("private").toString()}';"
                webView?.evaluateJavascript(jsCode, null)
            }
        }
        webView?.loadUrl(url.toString())

        return binding.root
    }

    /**
     * 当视图被销毁时调用，释放资源以避免内存泄漏。
     */
    override fun onDestroyView() {
        super.onDestroyView()

        // 删除缓存
        if (File(modCacheDir).exists()) {
            File(modCacheDir).deleteRecursively()
        }

        // 清理 WebView 资源
        webView?.let { web ->
            web.stopLoading()
            web.webViewClient = WebViewClient() // 设置默认的 WebViewClient
            web.settings.javaScriptEnabled = false
            web.clearHistory()
            web.clearView()
            web.removeAllViews()
            web.destroyDrawingCache()
            web.destroy()
            web.setTag(null)
            (web.parent as? ViewGroup)?.removeView(web)

            // 清除 JavaScript 代码
            web.evaluateJavascript("window.location.href='about:blank'", null)
        }

        // 清理绑定引用
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        webView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView?.onResume()
    }
}