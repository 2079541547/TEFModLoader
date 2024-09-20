package silkways.terraria.efmodloader.ui.gametool

import android.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.logic.ApplicationSettings.isDarkThemeEnabled
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RunningLogs : Fragment() {

    private lateinit var scrollView: ScrollView
    private lateinit var textView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val logBuilder = StringBuilder()
    private val SeelogBuilder = StringBuilder()

    companion object {
        private const val MAX_LOG_LINES = 1001
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 创建一个透明背景的ScrollView
        scrollView = ScrollView(context)
        scrollView.setBackgroundColor(0x00000000) // 设置背景为透明

        // 创建TextView并设置其属性
        textView = TextView(context)
        textView.text = "" // 初始为空
        textView.setTextColor(resources.getColor(R.color.md_theme_onSurface_highContrast))
        textView.movementMethod = ScrollingMovementMethod() // 允许滚动


        if (isDarkThemeEnabled(context)) {
            textView.setTextColor(resources.getColor(R.color.md_theme_onPrimary))
        }

        // 为TextView添加长按监听器
        textView.setOnLongClickListener {
            exportLogsToFile()
            true
        }

        // 将TextView添加到ScrollView中
        scrollView.addView(textView)

        // 开始定时更新TextView中的内容
        updateLogs()

        return scrollView
    }

    private fun updateLogs() {
        // 捕捉日志
        val newLogs = captureLogs()
        // 更新日志
        updateLogContent(newLogs)

        // 每隔一秒更新一次
        handler.postDelayed({
            updateLogs()
        }, 1000)
    }

    private fun captureLogs(): String {
        val builder = StringBuilder()

        // 执行logcat命令来获取所有日志
        try {
            val process = Runtime.getRuntime().exec("logcat -d") // -d 表示导出所有日志
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    builder.append(line).append("\n")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return builder.toString()
    }

    private fun updateLogContent(newLogs: String) {

        logBuilder.append(newLogs)
        // 将新的日志添加到现有的日志中
        SeelogBuilder.append(newLogs)

        // 拆分现有日志为行
        val lines = SeelogBuilder.toString().split("\n").toMutableList()

        // 如果超过了最大行数，删除最早的行
        while (lines.size > MAX_LOG_LINES) {
            lines.removeAt(0) // 删除第一行
        }

        // 重建日志字符串
        SeelogBuilder.setLength(0) // 清空StringBuilder的内容
        SeelogBuilder.append(lines.joinToString("\n"))

        // 更新TextView内容
        textView.text = SeelogBuilder.toString()
    }

    private fun exportLogsToFile() {
        // 获取当前时间戳作为文件名
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "$timestamp.log"

        // 创建文件路径
        val dir = context?.getExternalFilesDir("Logs") ?: return
        val file = File(dir, fileName)

        // 写入文件
        try {
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos).use { writer ->
                    writer.write(logBuilder.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "导出失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 移除所有的回调和消息，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
    }
}