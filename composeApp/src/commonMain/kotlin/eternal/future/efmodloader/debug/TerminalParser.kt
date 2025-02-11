package eternal.future.efmodloader.debug

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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