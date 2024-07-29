package silkways.terraria.toolbox.logic.game


import com.android.tools.build.apkzlib.zip.AlignmentRules
import com.android.tools.build.apkzlib.zip.ZFile
import com.android.tools.build.apkzlib.zip.ZFileOptions
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException


/**
 * 提供压缩目录到ZIP文件的功能。
 */

object AddRes {
    private val Z_FILE_OPTIONS: ZFileOptions = ZFileOptions().setAlignmentRule(
        AlignmentRules.compose(
            AlignmentRules.constantForSuffix(".so", 4096),
            AlignmentRules.constantForSuffix(".assets", 4096)
        )
    )

    fun addLib(srcApkPath: String, outputApk: String, libPath: Array<String>){
        val srcApk = File(srcApkPath)
        val outApk = File(outputApk)
        ZFile.openReadWrite(srcApk, Z_FILE_OPTIONS).use { efZFile ->
            efZFile.add("/lib/0", ByteArrayInputStream(outApk.readBytes()), false)
        }
    }

}
