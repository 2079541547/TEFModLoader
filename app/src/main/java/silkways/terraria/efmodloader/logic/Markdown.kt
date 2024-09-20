package silkways.terraria.efmodloader.logic

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
    fun markdownToHtml(markdown: String, colorScheme: Any?): String {
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
                    color: ${if (colorScheme == 1) "#000" else if (colorScheme == 2) "#fff" else "auto"};
                    background-color: ${if (colorScheme == 1) "#fff" else if (colorScheme == 2) "#0F1416" else "auto"};
                }
                hr {
                    border: none;
                    height: 1px;
                    background-color: ${if (colorScheme == 1) "#4C626B" else if (colorScheme == 2) "#B3CAD5" else "auto"};
                }
                
                /* 导航链接样式 */
                div.nav-links a {
                    display: inline-block;
                    margin-right: 1em;
                    text-decoration: none;
                    color: ${if (colorScheme == 1) "#226488" else if (colorScheme == 2) "#92CDF6" else "auto"} !important;
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