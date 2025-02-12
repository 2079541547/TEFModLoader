package eternal.future.efmodloader.utility

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import eternal.future.efmodloader.State
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object EFLog {

    private var logFilePath = File(App.getPrivate(), "jvm-runtime.log").path
    private var printCallerInfo = true
    private val TAG = "TEFModLoader"


    init {
        ensureLogFile()
    }

    private fun ensureLogFile() {
        if (!State.loggingEnabled.value) return
        val file = File(logFilePath)
        if (!file.exists()) {
            file.createNewFile()
        } else if (file.length() > State.logCache.value) {
            rotateLogFile(file)
        }
    }

    private fun rotateLogFile(file: File) {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        val newFileName = "${logFilePath}.${sdf.format(Date())}"
        file.renameTo(File(newFileName))
        file.createNewFile()
    }

    private fun writeToFile(message: String) {
        if (State.loggingEnabled.value) {
            File(logFilePath).appendText("$message\n")
        }
    }

    private fun formatMessage(severity: Severity, tag: String, message: String, callerInfo: Triple<String, String, Int>): String {
        val callerString = if (callerInfo.third != -1) " (${callerInfo.first}:${callerInfo.second}:${callerInfo.third})" else ""
        return "[${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())}] $severity/$tag$callerString: $message"
    }

    private fun getCallerInfo(): Triple<String, String, Int> {
        val stackTrace = Thread.currentThread().stackTrace
        for (element in stackTrace) {
            if ("$${'$'}logger" !in element.className && EFLog::class.java.name != element.className) {
                return Triple(element.className, element.fileName ?: "Unknown", element.lineNumber)
            }
        }
        return Triple("Unknown", "Unknown", -1)
    }

    fun setPrintCallerInfo(enabled: Boolean) {
        printCallerInfo = enabled
    }

    fun v(throwable: Throwable? = null, tag: String = TAG, message: () -> String) {
        log(Severity.Verbose, tag, message(), throwable)
    }

    fun v(messageString: String, throwable: Throwable? = null, tag: String = TAG) {
        log(Severity.Verbose, tag, messageString, throwable)
    }

    fun d(throwable: Throwable? = null, tag: String = TAG, message: () -> String) {
        log(Severity.Debug, tag, message(), throwable)
    }

    fun d(messageString: String, throwable: Throwable? = null, tag: String = TAG) {
        log(Severity.Debug, tag, messageString, throwable)
    }

    fun i(throwable: Throwable? = null, tag: String = TAG, message: () -> String) {
        log(Severity.Info, tag, message(), throwable)
    }

    fun i(messageString: String, throwable: Throwable? = null, tag: String = TAG) {
        log(Severity.Info, tag, messageString, throwable)
    }

    fun w(throwable: Throwable? = null, tag: String = TAG, message: () -> String) {
        log(Severity.Warn, tag, message(), throwable)
    }

    fun w(messageString: String, throwable: Throwable? = null, tag: String = TAG) {
        log(Severity.Warn, tag, messageString, throwable)
    }

    fun e(throwable: Throwable? = null, tag: String = TAG, message: () -> String) {
        log(Severity.Error, tag, message(), throwable)
    }

    fun e(messageString: String, throwable: Throwable? = null, tag: String = TAG) {
        log(Severity.Error, tag, messageString, throwable)
    }

    private fun log(severity: Severity, tag: String, message: String, throwable: Throwable?) {
        when (severity) {
            Severity.Verbose -> Logger.v(tag) { message }
            Severity.Debug -> Logger.d(tag) { message }
            Severity.Info -> Logger.i(tag) { message }
            Severity.Warn -> Logger.w(tag) { message }
            Severity.Error -> Logger.e(tag) { message }
            else -> Logger.v(tag) { message }
        }
        throwable?.let {
            Logger.e(tag) { it.stackTraceToString() }
        }

        val callerInfo = if (printCallerInfo) getCallerInfo() else Triple("", "", -1)
        val formattedMessage = formatMessage(severity, tag, message, callerInfo)
        writeToFile(formattedMessage)

        throwable?.let {
            writeToFile(it.stackTraceToString())
        }
    }
}