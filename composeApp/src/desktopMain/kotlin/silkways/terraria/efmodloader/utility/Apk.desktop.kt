package silkways.terraria.efmodloader.utility

import com.bigzhao.xml2axml.AxmlUtils
import java.io.File

actual fun Apk.encoderAXml(
    inputPath: String,
    outputPath: String
) {
   File(outputPath).writeBytes(AxmlUtils.encode(File(inputPath)))
}