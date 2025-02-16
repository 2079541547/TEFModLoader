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
    private const val TAG = "TEFModLoader"


    init {
        ensureLogFile()
    }

    private fun ensureLogFile() {
        if (!State.loggingEnabled.value) return
        val file = File(logFilePath)
        if (!file.exists()) {
            file.createNewFile()
        } else if (file.length() > State.logCache.value && State.logCache.value != -1) {
            clearLogFile(file)
        }
    }

    private fun clearLogFile(file: File) {
        file.writeText("")
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
        for (i in 3 until stackTrace.size) {
            val element = stackTrace[i]
            if ("EFLog" != element.className && EFLog::class.java.name != element.className) {
                var methodName = "Unknown"
                if (i + 1 < stackTrace.size) {
                    methodName = stackTrace[i + 1].methodName
                }
                return Triple("${element.className}.${methodName}", element.fileName ?: "Unknown", element.lineNumber)
            }
        }
        return Triple("Unknown.Unknown", "Unknown", -1)
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
        val callerInfo = if (printCallerInfo) getCallerInfo() else Triple("", "", -1)

        when (severity) {
            Severity.Verbose -> Logger.v(tag) { "[${callerInfo.first}, ${callerInfo.second}:${callerInfo.third}]: $message" }
            Severity.Debug -> Logger.d(tag) { "[${callerInfo.first}, ${callerInfo.second}:${callerInfo.third}]: $message" }
            Severity.Info -> Logger.i(tag) { "[${callerInfo.first}, ${callerInfo.second}:${callerInfo.third}]: $message" }
            Severity.Warn -> Logger.w(tag) { "[${callerInfo.first}, ${callerInfo.second}:${callerInfo.third}]: $message" }
            Severity.Error -> Logger.e(tag) { "[${callerInfo.first}, ${callerInfo.second}:${callerInfo.third}]: $message" }
            else -> Logger.v(tag) { "[${callerInfo.first}, ${callerInfo.second}:${callerInfo.third}]: $message" }
        }

        throwable?.let {
            Logger.e(tag) { it.stackTraceToString() }
        }

        val formattedMessage = formatMessage(severity, tag, message, callerInfo)
        writeToFile(formattedMessage)

        throwable?.let {
            writeToFile(it.stackTraceToString())
        }
    }
}