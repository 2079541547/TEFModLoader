package silkways.terraria.efmodloader.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.ActivitySettingBinding
import silkways.terraria.efmodloader.databinding.ActivityWebBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.Markdown
import kotlin.toString

/*******************************************************************************
 * 文件名称: WebActivity
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 上午3:29
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

class WebActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWebBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)

        val isMod = intent.getBooleanExtra("isMod", false)
        val webUrl: String = intent.getStringExtra("webUrl").toString()

        setContentView(binding.root)

        val webView = binding.WebView
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webView.clearCache(true)

        if (isMod) {
            webView.webViewClient = object : WebViewClient() {
                @SuppressLint("ClickableViewAccessibility")
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val jsCode = "PrivateDirectory = '${intent.getStringExtra("private").toString()}';"
                    webView.evaluateJavascript(jsCode, null)
                }
            }
            webView.loadUrl(webUrl)
        } else {
            val markdownContent = Markdown.loadMarkdownFromAssets(
                this,
                LanguageHelper.getMDLanguage(
                    JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.languageKey),
                    this,
                    webUrl
                ),
            )

            val htmlContent = Markdown.markdownToHtml(markdownContent, JsonConfigModifier.readJsonValue(this, Settings.jsonPath, Settings.themeKey))
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null)
        }



    }

}