package silkways.terraria.toolbox.logic

import org.commonmark.Extension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.BufferedReader
import java.io.InputStreamReader
import android.content.Context

object Markdown {
    /**
     * 从 assets 文件夹加载 Markdown 文件
     */
    fun loadMarkdownFromAssets(context: Context, fileName: String): String {
        val sb = StringBuilder()
        context.assets.open(fileName).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
                reader.lineSequence().forEach { line ->
                    sb.append(line).append("\n")
                }
            }
        }
        return sb.toString()
    }

    /**
     * 将 Markdown 字符串转换为 HTML
     */

    fun markdownToHtml(markdown: String): String {
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
                    color: #000; /* 默认字体颜色 */
                    background-color: #fff; /* 默认背景颜色 */
                }
                hr {
                    border: none;
                    height: 1px;
                    background-color: #4C626B; /* 分割线的颜色 */
                }
                
                /* 导航链接样式 */
                div.nav-links a {
                    display: inline-block;
                    margin-right: 1em;
                    text-decoration: none;
                    color: #226488 !important; /* 链接颜色 */
                }
                
                /* 适应深色模式 */
                @media (prefers-color-scheme: dark) {
                    body {
                        color: #fff; /* 深色模式下的字体颜色 */
                        background-color: #0F1416; /* 深色模式下的背景颜色 */
                    }
                    hr {
                        background-color: #B3CAD5; /* 深色模式下的分割线颜色 */
                    }
                    div.nav-links a {
                        color: #92CDF6 !important; /* 深色模式下的链接颜色 */
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
        return header + htmlBody + footer
    }

}