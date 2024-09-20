package silkways.terraria.efmodloader.ui.gametool

import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.GametoolWebBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier

class OnlineVideo: Fragment() {
    private var _binding: GametoolWebBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        _binding = GametoolWebBinding.inflate(inflater, container, false)

        val webView = binding.WebView // 假设你的布局文件中已经有一个id为webView的WebView组件

        // 设置WebView的属性
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // 开启JavaScript支持
        webSettings.domStorageEnabled = true // 开启DOM storage
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings.builtInZoomControls = true // 启用缩放
        webSettings.displayZoomControls = false // 隐藏缩放按钮
        webSettings.loadsImagesAutomatically = true // 自动加载图片
        webSettings.setSupportMultipleWindows(true) // 支持多窗口

        // 如果你想在应用内处理链接点击而不是打开新的浏览器
        webView.webViewClient = WebViewClient()

        // 加载指定的网页
        webView.loadUrl(JsonConfigModifier.readJsonValue(activity, Settings.jsonPath, Settings.OnlineVideo)
            .toString())


        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}