package silkways.terraria.toolbox.logic

import java.io.*
import java.util.zip.*

object ApkBuilder {

    /**
     * 将指定目录打包为APK文件，其中assets目录下的所有文件和子目录以存储模式（不压缩）打包。
     *
     * @param sourceDir 需要打包的根目录
     * @param assetsDirName assets目录的名称，默认为"assets"
     * @param outputApkPath 输出的APK文件路径
     */
    @Throws(IOException::class)
    fun packDirectoryAsApkWithAssetsStored(sourceDir: File, outputApkPath: String) {
        val outputFile = File(outputApkPath)
        val assetsDirName = "assets"
        // 确保输出目录的父目录存在
        outputFile.parentFile?.mkdirs()

        val zipOutputStream = ZipOutputStream(FileOutputStream(outputFile), Charsets.UTF_8)

        // 遍历源目录，递归打包
        addDirectoryRecursively(sourceDir, "", zipOutputStream, assetsDirName)

        zipOutputStream.finish()
        zipOutputStream.close()
    }

    // 修复后的代码段
    private fun addDirectoryRecursively(dir: File, basePath: String, zipOutputStream: ZipOutputStream, assetsDirName: String) {
        dir.listFiles()?.forEach { file ->
            val entryName = "$basePath${if (basePath.isEmpty()) "" else "/"}${file.name}"
            val zipEntry = ZipEntry(entryName)

            // 当前是文件且位于assets目录下，才使用STORED方法
            val isStoreMethodNeeded = file.isFile && entryName.startsWith("$assetsDirName/")
            if (isStoreMethodNeeded) {
                // 设置为STORED方法
                zipEntry.method = ZipEntry.STORED

                // 计算并设置正确的CRC32和大小
                val crcValue = calculateCrc(file)
                val fileSize = file.length()
                zipEntry.crc = crcValue.toInt().toLong() // 注意：这里需要转换为Int类型
                zipEntry.size = fileSize
                zipEntry.compressedSize = fileSize // 未压缩大小等于原文件大小
            }

            zipOutputStream.putNextEntry(zipEntry)

            if (file.isDirectory) {
                addDirectoryRecursively(file, entryName, zipOutputStream, assetsDirName)
            } else {
                FileInputStream(file).use { inputStream ->
                    inputStream.copyTo(zipOutputStream.buffered())
                }
            }

            zipOutputStream.closeEntry()
        }
    }

    @Throws(IOException::class)
    private fun calculateCrc(file: File): Long {
        FileInputStream(file).use { fis ->
            val crc = CRC32()
            val buffer = ByteArray(8192)
            var length: Int
            while (fis.read(buffer).also { length = it } != -1) {
                crc.update(buffer, 0, length)
            }
            return crc.value
        }
    }

}
