package silkways.terraria.efmodloader.debug

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


data class LogEntry(
    val timestamp: String,
    val source: Source,
    val type: LogType,
    val message: String
)

enum class Source { KOTLIN, OTHER }
enum class LogType(val color: Color) {
    INFO(Color.Green),
    WARNING(Color.Yellow),
    ERROR(Color.Red),
    DEBUG(Color.Gray)
}

class TerminalParser {

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val logEntries = mutableListOf<LogEntry>()

    fun addLogEntry(source: Source, type: LogType, message: String) {
        val timestamp = LocalDateTime.now().format(dateTimeFormatter)
        logEntries.add(LogEntry(timestamp, source, LogType.INFO, message))
    }

    suspend fun parse(command: String): String {
        delay(500)
        return "Command '$command' executed"
    }

    fun getLogEntries(): List<LogEntry> {
        return logEntries.toList()
    }
}