package silkways.terraria.efmodloader.utility

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object Zip {
    fun copyZipFromResources(fileName: String, destinationDir: String): String {
        val inputStream: InputStream = object {}.javaClass.getResourceAsStream("/$fileName") ?: throw FileNotFoundException("Resource not found: $fileName")
        val destFile = File(destinationDir, fileName)

        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()

        try {
            FileOutputStream(destFile).use { fos ->
                inputStream.copyTo(fos)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return destFile.absolutePath
    }

    fun unzipSpecificFilesIgnorePath(zipFilePath: String, destDirectory: String, vararg fileNames: String) {
        FileInputStream(zipFilePath).use { fis ->
            ZipInputStream(fis).use { zis ->
                var entry: ZipEntry?
                while (zis.nextEntry.also { entry = it } != null) {
                    val fileName = entry!!.name
                    if (fileNames.any { fileName.contains(it) }) {
                        FileOutputStream(destDirectory).use { fos ->
                            zis.copyTo(fos)
                        }
                    }
                    zis.closeEntry()
                }
            }
        }
    }
}