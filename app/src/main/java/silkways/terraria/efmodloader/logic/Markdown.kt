package silkways.terraria.efmodloader.logic

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

object Markdown {

    /**
     * 从 assets 文件夹加载 Markdown 文件。
     *
     * @param context 应用程序上下文。
     * @param fileName Markdown 文件名。
     * @return 加载的 Markdown 内容字符串。
     */
    fun loadMarkdownFromAssets(context: Context, fileName: String): String {
        val sb = StringBuilder()
        try {
            context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8"))).use { reader ->
                    reader.lineSequence().forEach { line ->
                        sb.append(line).append("\n")
                    }
                }
            }
            EFLog.i("Markdown 文件加载完成: $fileName")
        } catch (e: Exception) {
            EFLog.e("加载 Markdown 文件时发生错误: $fileName - ${e.message}")
        }
        return sb.toString()
    }

    /**
     * 将 Markdown 字符串转换为 HTML。
     *
     * @param markdown Markdown 内容字符串。
     * @param isDarkTheme 当前是否为暗黑模式。
     * @return 转换后的 HTML 字符串。
     */
    fun markdownToHtml(markdown: String, isDarkTheme: Boolean): String {
        val extensions: List<Extension> = emptyList() // 不需要任何扩展
        val parser = Parser.builder().extensions(extensions).build()
        val renderer = HtmlRenderer.builder().extensions(extensions).build()
        val document = parser.parse(markdown)

        // 创建 HTML 文档的头部
        val header = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body {
                    font-size: 28px;
                    color: ${if (isDarkTheme) "#fff" else "#000"};
                    background-color: ${if (isDarkTheme) "#0F1416" else "#fff"};
                }
                hr {
                    border: none;
                    height: 1px;
                    background-color: ${if (isDarkTheme) "#B3CAD5" else "#4C626B"};
                }
                
                /* 导航链接样式 */
                div.nav-links a {
                    display: inline-block;
                    margin-right: 1em;
                    text-decoration: none;
                    color: ${if (isDarkTheme) "#92CDF6" else "#226488"};
                }

                /* 媒体查询 */
                @media (prefers-color-scheme: dark) {
                    :root {
                        --text-color: #fff;
                        --bg-color: #0F1416;
                        --hr-color: #B3CAD5;
                        --nav-link-color: #92CDF6;
                    }
                }

                @media (prefers-color-scheme: light) {
                    :root {
                        --text-color: #000;
                        --bg-color: #fff;
                        --hr-color: #4C626B;
                        --nav-link-color: #226488;
                    }
                }
            </style>
        </head>
        <body>
    """.trimIndent()

        // 创建 HTML 文档的尾部
        val footer = "</body></html>".trimIndent()

        // 将 Markdown 转换为 HTML
        val htmlBody = renderer.render(document)

        // 合并头部、正文和尾部
        val finalHtml = header + htmlBody + footer

        EFLog.i("Markdown 转换为 HTML 完成")
        EFLog.d("Markdown 内容: $markdown")
        EFLog.d("HTML 内容: $finalHtml")

        return finalHtml
    }
}

/**
 * Composable 函数，用于加载 Markdown 文件并转换为 HTML，根据当前主题调整样式。
 */
@Composable
fun MarkdownContent(fileName: String) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()

    val markdownContent = remember {
        Markdown.loadMarkdownFromAssets(context, fileName)
    }

    val htmlContent = remember(markdownContent, isDarkTheme) {
        Markdown.markdownToHtml(markdownContent, isDarkTheme)
    }

    // 在这里可以使用 WebView 或其他方式显示 HTML 内容
    // 例如：AndroidView(factory = { WebView(it).apply { loadData(htmlContent, "text/html", "UTF-8") } })
}