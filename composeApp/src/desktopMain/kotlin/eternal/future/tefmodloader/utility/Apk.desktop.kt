package eternal.future.tefmodloader.utility

import com.bigzhao.xml2axml.AxmlUtils
import java.io.File
import cn.zaratustra.axmlparser.core.AXMLParser

actual fun Apk.encoderAXml(
    inputPath: String,
    outputPath: String
) {
   File(outputPath).writeBytes(AxmlUtils.encode(File(inputPath)))
}

actual fun Apk.decodeAXml(
    inputPath: String,
    outputPath: String
) {
    val axmlParser = AXMLParser()
    axmlParser.parseToXML(inputPath, outputPath)
}