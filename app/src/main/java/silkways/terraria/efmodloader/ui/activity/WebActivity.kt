package silkways.terraria.efmodloader.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.rememberNavHostEngine
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.logic.ApplicationSettings.isDarkThemeEnabled
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.Markdown
import silkways.terraria.efmodloader.ui.screen.CustomTopBar
import silkways.terraria.efmodloader.ui.screen.NavGraphs
import silkways.terraria.efmodloader.ui.screen.SettingsScreen
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.ui.utils.LocalSnackbarHost
import silkways.terraria.efmodloader.utils.SPUtils

/*******************************************************************************
 * 文件名称: WebActivity
 * 项目名称: TEFModLoader-Compose
 * 创建时间: 2024/11/16 上午11:49
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
 * 描述信息: 本文件为TEFModLoader-Compose项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

class WebActivity: EFActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)

        intent.getStringExtra("Title").toString()
        intent.getBooleanExtra("isMod", false)
        intent.getStringExtra("webUrl").toString()

        setContent {
            TEFModLoaderComposeTheme(darkTheme = isDarkThemeEnabled(this)) {
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                val navHostEngine = rememberNavHostEngine(
                    navHostContentAlignment = Alignment.TopCenter,
                    rootDefaultAnimations = RootNavGraphDefaultAnimations(
                        enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                        exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                    ),
                    defaultAnimationsForNestedNavGraph = mapOf(
                        NavGraphs.root to NestedNavGraphDefaultAnimations(
                            enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                            exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                        )
                    )
                )

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { paddingValues ->
                    CompositionLocalProvider(LocalSnackbarHost provides snackBarHostState) {
                        DestinationsNavHost(
                            modifier = Modifier.padding(paddingValues),
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine
                        )
                        WebViewComposable(
                            title = intent.getStringExtra("Title").toString(),
                            isMod = intent.getBooleanExtra("isMod", false),
                            webUrl = intent.getStringExtra("webUrl").toString(),
                            privateDirectory = intent.getStringExtra("private")
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewComposable(
    title: String,
    isMod: Boolean,
    webUrl: String,
    privateDirectory: String? = null
) {
    val context = LocalContext.current
    val isDarkTheme = isDarkThemeEnabled(context)

    val markdownContent = Markdown.loadMarkdownFromAssets(
        context,
        LanguageHelper.getMDLanguage(
            SPUtils.readInt(Settings.languageKey, 0),
            context,
            webUrl
        )
    )

    val htmlContent = Markdown.markdownToHtml(markdownContent, isDarkTheme)

    Scaffold(
        topBar = {
            CustomTopBar(title)
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp) // 确保内容不会被顶部栏遮挡
                    .padding(horizontal = 10.dp),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = {
                                WebView(it).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    settings.javaScriptEnabled = true
                                    settings.useWideViewPort = true
                                    settings.loadWithOverviewMode = true
                                    settings.cacheMode = WebSettings.LOAD_NO_CACHE
                                    clearCache(true)
                                }
                            },
                            update = { webView ->
                                if (isMod) {
                                    webView.webViewClient = object : WebViewClient() {
                                        @SuppressLint("ClickableViewAccessibility")
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            privateDirectory?.let {
                                                val jsCode = "PrivateDirectory = '$it';"
                                                webView.evaluateJavascript(jsCode, null)
                                            }
                                        }
                                    }
                                    webView.loadUrl(webUrl)
                                } else {


                                    webView.loadDataWithBaseURL(
                                        null,
                                        htmlContent,
                                        "text/html",
                                        "utf-8",
                                        null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    )
}


