package silkways.terraria.toolbox.logic

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
import org.apache.commons.compress.utils.IOUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry

/**
 * 提供压缩目录到ZIP文件的功能。
 */
object AddRes {
    fun compressDirectoryToZip(inputDir: File, outputFilePath: String) {
        val outputFile = File(outputFilePath)
        if (outputFile.exists()) outputFile.delete() // 删除已存在的文件以覆盖

        val outputStream = FileOutputStream(outputFile)
        val zipOut = ZipArchiveOutputStream(outputStream)

        compressRecursively(inputDir, inputDir, "", zipOut)

        zipOut.finish()
        zipOut.close()
        outputStream.close()
    }

    private fun compressRecursively(file: File, rootDir: File, parentPath: String, zipOut: ZipArchiveOutputStream) {
        if (file.isDirectory) {
            for (child in file.listFiles()!!) {
                val adjustedParentPath = if (file == rootDir) "" else "$parentPath${file.name}/"
                compressRecursively(child, rootDir, adjustedParentPath, zipOut)
            }
        } else {
            val entryName = if (file.parentFile == rootDir) file.name else "$parentPath${file.name}"
            val zipEntry = ZipArchiveEntry(file, entryName)
            zipEntry.method = ZipEntry.STORED

            // 计算文件的CRC32值
            val crc = CRC32()
            val fis = FileInputStream(file)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                crc.update(buffer, 0, bytesRead)
            }
            fis.close()

            // 设置CRC和大小
            zipEntry.crc = crc.value
            zipEntry.size = file.length()

            zipOut.putArchiveEntry(zipEntry)
            val inputStream = FileInputStream(file)
            IOUtils.copy(inputStream, zipOut)
            inputStream.close()
            zipOut.closeArchiveEntry()
        }
    }
}
