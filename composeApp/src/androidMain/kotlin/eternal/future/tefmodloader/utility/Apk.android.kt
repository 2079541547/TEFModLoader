package eternal.future.tefmodloader.utility

import android.content.pm.PackageManager
import androidx.core.net.toUri
import eternal.future.tefmodloader.MainApplication
import mt.modder.hub.axml.AXMLCompiler
import mt.modder.hub.axml.AXMLPrinter
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile


fun Apk.extractWithPackageName(packageName: String, targetPath: String) {
    try {
        val pm = MainApplication.getContext().packageManager
        val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)

        packageInfo.applicationInfo?.sourceDir?.let { sourceDir ->
            val sourceFile = File(sourceDir)
            val targetFile = File(targetPath)

            if (!targetFile.exists()) {
                sourceFile.copyTo(targetFile, overwrite = false)
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Apk.copyApk(apkPath: String, targetPath: String) {
    val url = apkPath.toUri()
    MainApplication.getContext().contentResolver.openInputStream(url)?.use { inputStream ->
        FileOutputStream(targetPath).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
    }
}


fun Apk.doesAnyAppContainMetadata(metadataKey: String): Boolean {
    val packageManager = MainApplication.getContext().packageManager
    val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    for (packageInfo in packages) {
        try {
            if (packageInfo.metaData?.containsKey(metadataKey) == true) {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return false
}

fun Apk.getPackageNamesWithMetadata(metadataKey: String): Map<String, Int> {
    val context = MainApplication.getContext()
    val packageManager = context.packageManager
    val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    return packages.mapNotNull { appInfo ->
        try {
            appInfo.metaData?.get(metadataKey)?.let { value ->
                val intValue = value as? Int ?: 0
                appInfo.packageName to intValue
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }.toMap()
}

fun Apk.launchAppByPackageName(packageName: String): Boolean {
    return try {
        val context = MainApplication.getContext()
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
            true
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun Apk.getSupportedAbi(packageName: String): String? {
    return try {
        val appInfo = MainApplication.getContext().packageManager
            .getApplicationInfo(packageName, 0)
        val apkPath = appInfo.sourceDir

        val abis = mutableSetOf<String>()

        ZipFile(apkPath).use { zip ->

            zip.entries().iterator().forEach { entry ->
                if (entry.name.startsWith("lib/")) {
                    entry.name.split('/').getOrNull(1)?.let { abi ->
                        abis.add(abi)
                    }
                }
            }

            abis.firstOrNull { it.contains("arm64") } ?:
            abis.firstOrNull { it.contains("armeabi") } ?:
            abis.firstOrNull { it.contains("x86_64") } ?:
            abis.firstOrNull { it.contains("x86") } ?:
            abis.firstOrNull()
        }.also { abi ->
            EFLog.i("解析APK获取到支持ABI: ${abis.joinToString()}, 最终选择: $abi")
        }
    } catch (e: Exception) {
        EFLog.e("获取游戏ABI失败: ${e.message}")
        null
    }
}

actual fun Apk.encoderAXml(
    inputPath: String,
    outputPath: String
) {

    File(outputPath).writeBytes(AXMLCompiler().axml2Xml(MainApplication.getContext(), File(inputPath).readText()))
}

actual fun Apk.decodeAXml(
    inputPath: String,
    outputPath: String
) {
    val axmlPrinter = AXMLPrinter()
    axmlPrinter.setEnableID2Name(false)
    axmlPrinter.setAttrValueTranslation(false)
    axmlPrinter.setExtractPermissionDescription(false)

    val xmlString = axmlPrinter.readFromFile(inputPath)
    File(outputPath).writeText(xmlString)
}