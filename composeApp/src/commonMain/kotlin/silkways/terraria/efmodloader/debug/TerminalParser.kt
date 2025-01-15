package silkways.terraria.efmodloader.debug

import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TerminalParser {
    private val commands = mutableMapOf<String, (Array<String>) -> String>()

    init {
        registerCommand("ls") { args ->
            when(args.size) {
                1 -> {
                    val files = File(args[0]).listFiles()?.filterNotNull()
                    val fileList = files?.map { it.name } ?: emptyList()
                    if (fileList.isEmpty()) {
                        "No items found in the current directory."
                    } else {
                        fileList.joinToString(separator = "\n")
                    }
                }
                2 -> {
                    if (args[0] == "-al") getFileDetails(args[1]) else "Usage: ls <path> or ls -al <path>"
                }
                else -> "Usage: ls <path> or ls -al <path>"
            }
        }
    }

    private fun getFileDetails(path: String): String {
        val format = SimpleDateFormat("MMM dd HH:mm", Locale.getDefault())
        val builder = StringBuilder()

        File(path).takeIf { it.isDirectory }?.listFiles()?.filterNotNull()?.forEach { file ->
            val filePath = file.toPath()
            val attributes = Files.readAttributes(filePath, BasicFileAttributes::class.java)
            val date = Date(attributes.lastModifiedTime().toMillis())

            val permissions = try {
                Files.getPosixFilePermissions(filePath).joinToString("") {
                    when (it) {
                        PosixFilePermission.OWNER_READ -> "r"
                        PosixFilePermission.OWNER_WRITE -> "w"
                        PosixFilePermission.OWNER_EXECUTE -> "x"
                        PosixFilePermission.GROUP_READ -> "r"
                        PosixFilePermission.GROUP_WRITE -> "w"
                        PosixFilePermission.GROUP_EXECUTE -> "x"
                        PosixFilePermission.OTHERS_READ -> "r"
                        PosixFilePermission.OTHERS_WRITE -> "w"
                        PosixFilePermission.OTHERS_EXECUTE -> "x"
                        else -> "-"
                    }
                }.padStart(9, '-')
            } catch (e: UnsupportedOperationException) {
                "----------".padStart(9, '-')
            }

            val fileType = if (Files.isDirectory(filePath)) "d" else "-"

            val owner = try {
                Files.getOwner(filePath).name
            } catch (e: UnsupportedOperationException) {
                "unknown"
            }

            val group = try {
                val view = Files.getFileAttributeView(filePath, PosixFileAttributeView::class.java)
                view.readAttributes().group().name
            } catch (e: UnsupportedOperationException) {
                "unknown"
            }

            val size = attributes.size()
            builder.append("$fileType$permissions  ${file.listFiles()?.size ?: 0} $owner $group ${formatSize(size)} ${format.format(date)}  ${file.name}\n")
        }

        return if (builder.isEmpty()) "No items found in the current directory." else builder.toString()
    }

    private fun formatSize(size: Long): String {
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var fileSize = size.toDouble()
        var unitIndex = 0
        while (fileSize >= 1024 && unitIndex < units.size - 1) {
            fileSize /= 1024
            unitIndex++
        }
        return String.format("%4.0f %s", fileSize, units[unitIndex])
    }

    private fun registerCommand(name: String, action: (Array<String>) -> String) {
        commands[name] = action
    }

    fun parser(commandLine: Array<String>): String {
        val commandName = commandLine[0]
        val args = commandLine.drop(1).toTypedArray()
        return commands[commandName]?.invoke(args) ?: "Unknown command: $commandName"
    }
}