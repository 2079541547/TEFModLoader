package silkways.terraria.efmodloader.ui.gametool

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Fragment
import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.provider.Settings
import android.system.Os
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.TextView
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class GameStatus : Fragment() {

    private lateinit var scrollView: ScrollView
    private lateinit var textView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var context: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 创建一个透明背景的ScrollView
        scrollView = ScrollView(context)
        scrollView.setBackgroundColor(0x00000000) // 设置背景为透明

        // 创建TextView并设置其属性
        textView = TextView(context)
        textView.text = "" // 初始为空
        textView.setTextColor(resources.getColor(com.google.android.material.R.color.material_dynamic_neutral20))


        // 将TextView添加到ScrollView中
        scrollView.addView(textView)

        // 开始定时更新TextView中的内容
        updateSystemInfo()

        return scrollView
    }

    private fun updateSystemInfo() {
        val info = StringBuilder()

        // 获取运行信息
        info.append(getRuntimeInfo())

        // 获取系统信息
        info.append(getSystemInfo())

        // 更新TextView内容
        textView.text = info.toString()

        // 每隔一秒更新一次
        handler.postDelayed({
            updateSystemInfo()
        }, 1000)
    }

    private fun getRuntimeInfo(): String {
        val info = StringBuilder()
        try {
            // 获取当前进程ID
            val pid = android.os.Process.myPid()
            info.append("PID: $pid\n")

            // 获取当前进程的内存使用情况
            val memInfo = ActivityManager.MemoryInfo()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(memInfo)
            info.append("Total Memory: ${memInfo.totalMem} bytes / ${memInfo.totalMem / 1024} kilobytes / ${memInfo.totalMem / 1048576.000} megabytes / ${memInfo.totalMem / 1073741824.000} gigabytes\n")
            info.append("Available Memory: ${memInfo.availMem} bytes / ${memInfo.availMem / 1024} kilobytes / ${memInfo.availMem / 1048576.000} megabytes / ${memInfo.availMem / 1073741824.000} gigabytes\n")

            // 获取CPU频率
            val cpuFreqPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"
            val cpuFreq = readFile(cpuFreqPath).trim { it <= ' ' }.toLongOrNull()
            if (cpuFreq != null) {
                info.append("Current CPU Frequency: $cpuFreq Hz\n")
            } else {
                info.append("Current CPU Frequency: Not Available\n")
            }

            // 获取线程数量
            val threadCount = Thread.activeCount()
            info.append("Active Threads: $threadCount\n")

            // 获取帧率信息
            info.append("Current Frame Rate: ${getFrameRate()} fps\n")

            // 获取系统负载
            val loadAvg = readLoadAverage()
            info.append("System Load Average (1, 5, 15 mins): $loadAvg\n")



        } catch (e: Exception) {
            e.printStackTrace()
        }
        return info.toString()
    }

    private fun readLoadAverage(): String {
        val loadAvgFile = "/proc/loadavg"
        val content = readFile(loadAvgFile).split(" ")
        return content.getOrElse(0) { "N/A" } + ", " +
                content.getOrElse(1) { "N/A" } + ", " +
                content.getOrElse(2) { "N/A" }
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSystemInfo(): String {
        val info = StringBuilder()
        try {
            // 获取设备ID
            info.append("Device ID: ${Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)}\n")

            // 获取内部存储空间信息
            val statFs = StatFs(context.filesDir.path)
            val blockSize = statFs.blockSizeLong
            val availableBlocks = statFs.availableBlocksLong
            val totalBlocks = statFs.blockCountLong

            info.append("Internal Storage - Total: ${totalBlocks * blockSize} bytes / ${(totalBlocks * blockSize) / 1024} kilobytes / ${(totalBlocks * blockSize) / 1048576.000} megabytes / ${(totalBlocks * blockSize) / 1073741824.000} gigabytes\n")
            info.append("Internal Storage - Available: ${availableBlocks * blockSize} bytes / ${(availableBlocks * blockSize) / 1024} kilobytes / ${(availableBlocks * blockSize) / 1048576.000} megabytes / ${(availableBlocks * blockSize) / 1073741824.000} gigabytes\n")

            // 获取外部存储空间信息（如果存在）
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                val externalStatFs = StatFs(Environment.getExternalStorageDirectory().path)
                val externalBlockSize = externalStatFs.blockSizeLong
                val externalAvailableBlocks = externalStatFs.availableBlocksLong
                val externalTotalBlocks = externalStatFs.blockCountLong


                info.append("External Storage - Total: ${externalTotalBlocks * externalBlockSize} bytes / ${(externalTotalBlocks * externalBlockSize) / 1024} kilobytes / ${(externalTotalBlocks * externalBlockSize) / 1048576.000} megabytes / ${(externalTotalBlocks * externalBlockSize) / 1073741824.000} gigabytes\n")
                info.append("External Storage - Available: ${externalAvailableBlocks * externalBlockSize} bytes / ${(externalAvailableBlocks * externalBlockSize) / 1024} kilobytes / ${(externalAvailableBlocks * externalBlockSize) / 1048576.000} megabytes / ${(externalAvailableBlocks * externalBlockSize) / 1073741824.000} gigabytes\n")

            }

            // 获取电池状态
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            val batteryInfo = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            info.append("Battery Level: $batteryInfo%\n")

            // 获取屏幕分辨率
            val displayMetrics = resources.displayMetrics
            info.append("Screen Resolution: ${displayMetrics.widthPixels}x${displayMetrics.heightPixels}\n")

            // 获取操作系统版本
            info.append("Android Version: ${Build.VERSION.RELEASE}\n")
            info.append("API Level: ${Build.VERSION.SDK_INT}\n")

            // 获取设备制造商和型号
            info.append("Device Manufacturer: ${Build.MANUFACTURER}\n")
            info.append("Device Model: ${Build.MODEL}\n")

            //CPU
            val cpuInfo = getCPUInfo()
            info.append("CPU Info: $cpuInfo\n")

            // 获取SIM卡信息
            val telephonyManager = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            info.append("Carrier: ${telephonyManager.networkOperatorName}\n")

            // 获取内核版本
            info.append("Kernel Version: ${Os.uname().version}\n")

            // 获取JVM版本
            info.append("JVM Vendor: ${System.getProperty("java.vm.vendor")}\n")
            info.append("JVM Name: ${System.getProperty("java.vm.name")}\n")
            info.append("JVM Version: ${System.getProperty("java.vm.version")}\n")

            // 获取应用程序版本信息
            val packageManager = activity.packageManager
            val packageName = activity.packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            info.append("App Version: ${packageInfo.versionName}\n")
            info.append("Build Number: ${packageInfo.versionCode}\n")

            // 获取硬件信息
            info.append("CPU Architecture: ${Build.CPU_ABI}\n")
            info.append("Processor Count: ${Runtime.getRuntime().availableProcessors()}\n")

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return info.toString()
    }

    private fun getCPUInfo(): String {
        val `is` = Runtime.getRuntime().exec("cat /proc/cpuinfo").inputStream
        val reader = BufferedReader(InputStreamReader(`is`))
        var line: String? = reader.readLine()
        while (line != null) {
            if (line.startsWith("model name")) {
                return line
            }
            line = reader.readLine()
        }
        return "Unknown"
    }

    fun getFrameRate(): Float {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        return display.refreshRate
    }


    // 辅助函数用于读取系统文件
    private fun readFile(path: String): String {
        return try {
            // 使用InputStreamReader来创建一个字符流，指定UTF-8编码
            FileInputStream(path).use { fis ->
                InputStreamReader(fis, StandardCharsets.UTF_8).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: Exception) {
            // 处理异常情况
            Log.e("GameStatus", "Failed to read file at path: $path", e)
            ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 移除所有的回调和消息，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
    }
}