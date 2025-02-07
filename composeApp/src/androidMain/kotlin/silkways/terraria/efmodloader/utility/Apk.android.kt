package silkways.terraria.efmodloader.utility

import android.content.pm.PackageManager
import androidx.core.net.toUri
import mt.modder.hub.axml.AXMLCompiler
import silkways.terraria.efmodloader.MainApplication
import java.io.File
import java.io.FileOutputStream

fun Apk.extractWithPackageName(packageName: String, targetPath: String) {
    try {
        val pm = MainApplication.getContext().packageManager
        val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
        packageInfo.applicationInfo?.sourceDir?.let {
            File(it).let {
                if (!File(targetPath).exists()) it.copyTo(File(targetPath))
            }
        }
    } catch (e: ExceptionInInitializerError) {
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

actual fun Apk.encoderAXml(
    inputPath: String,
    outputPath: String
) {
    File(outputPath).writeBytes(AXMLCompiler().axml2Xml(MainApplication.getContext(), File(inputPath).readText()))
}