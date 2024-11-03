package silkways.terraria.efmodloader.logic

import android.content.Context
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
     * @param colorScheme 颜色方案，1 表示浅色模式，2 表示深色模式，其他值表示自动。
     * @return 转换后的 HTML 字符串。
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
                    color: ${if (colorScheme == 1) "#000" else if (colorScheme == 2) "#fff" else "var(--text-color)"};
                    background-color: ${if (colorScheme == 1) "#fff" else if (colorScheme == 2) "#0F1416" else "var(--bg-color)"};
                }
                hr {
                    border: none;
                    height: 1px;
                    background-color: ${if (colorScheme == 1) "#4C626B" else if (colorScheme == 2) "#B3CAD5" else "var(--hr-color)"};
                }
                
                /* 导航链接样式 */
                div.nav-links a {
                    display: inline-block;
                    margin-right: 1em;
                    text-decoration: none;
                    color: ${if (colorScheme == 1) "#226488" else if (colorScheme == 2) "#92CDF6" else "var(--nav-link-color)"};
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