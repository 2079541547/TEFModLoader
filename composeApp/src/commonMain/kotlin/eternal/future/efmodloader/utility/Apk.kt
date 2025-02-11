package eternal.future.efmodloader.utility

import cn.zaratustra.axmlparser.core.AXMLParser
import com.android.apksig.ApkSigner
import com.android.tools.build.apkzlib.sign.SigningExtension
import com.android.tools.build.apkzlib.sign.SigningOptions
import com.android.tools.build.apkzlib.zip.ZFile
import org.w3c.dom.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.cert.X509Certificate
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


object Apk {

    fun getSupportedAbis(apkPath: String): List<String> {
        val apkFile = File(apkPath)
        if (!apkFile.exists()) {
            println("APK file does not exist.")
            return emptyList()
        }

        val abis = HashSet<String>()

        try {
            ZipFile(apkFile).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val entryName = entry.name
                    if (entryName.startsWith("lib/") && !entryName.endsWith("/")) {
                        val abi = entryName.substringAfter("lib/").substringBefore('/')
                        abis.add(abi)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return abis.toList()
    }

    fun modifyManifest(filePath: String,
                       addDebuggable: Boolean, updateVersionCode: Boolean, packName: String = "") {
        val manifestFile = File(filePath)
        if (!manifestFile.exists()) {
            println("Manifest file does not exist: $filePath")
            return
        }

        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        val builder = factory.newDocumentBuilder()
        val document = builder.parse(manifestFile)
        document.documentElement.normalize()

        val manifestNode = document.documentElement

        if (packName != "") {
            manifestNode.setAttributeNS("", "package", packName)
        }

        fun addPermission(permissionName: String) {
            val permissionElement = document.createElement("uses-permission").apply {
                setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", permissionName)
            }
            manifestNode.appendChild(permissionElement)
            println("Added permission: $permissionName")
        }

        addPermission("android.permission.READ_EXTERNAL_STORAGE")
        addPermission("android.permission.WRITE_EXTERNAL_STORAGE")
        addPermission("android.permission.MANAGE_EXTERNAL_STORAGE")

        val applicationNodeList = manifestNode.getElementsByTagName("application")
        if (applicationNodeList.length == 0) {
            println("No <application> tag found in the manifest.")
            return
        }
        val applicationNode = applicationNodeList.item(0) as Element

        removeOldIntentFilters(applicationNode)

        val newActivityNode = createActivityElement(document, "eternal.future.TEFModLoader")
        applicationNode.appendChild(newActivityNode)

        if (addDebuggable) {
            applicationNode.setAttributeNS("http://schemas.android.com/apk/res/android", "android:debuggable", "true")
            println("Debuggable attribute set.")
        }

        if (updateVersionCode) {
            manifestNode.setAttributeNS("http://schemas.android.com/apk/res/android", "android:versionCode", "1")
            println("Version code updated.")
        }

        val metaDataNode = document.createElement("meta-data").apply {
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", "TEFModLoader")
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:value", "true")
        }
        applicationNode.appendChild(metaDataNode)
        println("Meta-data node added.")

        saveDocument(document, manifestFile)
    }

    private fun createActivityElement(document: Document, activityName: String): Element {
        val activityElement = document.createElement("activity")
        activityElement.setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", activityName)
        activityElement.setAttributeNS("http://schemas.android.com/apk/res/android", "android:exported", "true")

        val intentFilterElement = document.createElement("intent-filter")
        val actionElement = document.createElement("action").apply {
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", "android.intent.action.MAIN")
        }
        intentFilterElement.appendChild(actionElement)

        val categoryElement = document.createElement("category").apply {
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", "android.intent.category.LAUNCHER")
        }
        intentFilterElement.appendChild(categoryElement)

        activityElement.appendChild(intentFilterElement)
        println("Activity element created with intent-filter.")
        return activityElement
    }

    private fun removeOldIntentFilters(applicationNode: Element) {
        val activityNodes = applicationNode.getElementsByTagName("activity")
        for (i in activityNodes.length - 1 downTo 0) {
            val activityNode = activityNodes.item(i) as Element
            val intentFilterNodes = activityNode.getElementsByTagName("intent-filter")

            for (j in intentFilterNodes.length - 1 downTo 0) {
                val intentFilterNode = intentFilterNodes.item(j) as Element
                var hasMainAction = false
                var hasLauncherCategory = false

                val actionNodes = intentFilterNode.getElementsByTagName("action")
                for (k in 0 until actionNodes.length) {
                    val actionNode = actionNodes.item(k) as Element
                    if (actionNode.getAttributeNS("http://schemas.android.com/apk/res/android", "name") == "android.intent.action.MAIN") {
                        hasMainAction = true
                        break
                    }
                }

                val categoryNodes = intentFilterNode.getElementsByTagName("category")
                for (k in 0 until categoryNodes.length) {
                    val categoryNode = categoryNodes.item(k) as Element
                    if (categoryNode.getAttributeNS("http://schemas.android.com/apk/res/android", "name") == "android.intent.category.LAUNCHER") {
                        hasLauncherCategory = true
                        break
                    }
                }

                if (hasMainAction && hasLauncherCategory) {
                    activityNode.removeChild(intentFilterNode)
                    println("Removed old intent-filter from activity node.")
                }
            }
        }
    }

    private fun createServiceElement(document: Document, serviceName: String): Element {
        val serviceElement = document.createElement("service")
        serviceElement.setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", serviceName)
        serviceElement.setAttributeNS("http://schemas.android.com/apk/res/android", "android:exported", "true")

        /*
        val intentFilterElement = document.createElement("intent-filter")
        val actionElement = document.createElement("action").apply {
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", "com.example.SERVICE_ACTION")
        }
        intentFilterElement.appendChild(actionElement)
        serviceElement.appendChild(intentFilterElement)
        */

        println("Service element created.")
        return serviceElement
    }

    private fun saveDocument(document: Document, file: File) {
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer().apply {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        }
        val source = DOMSource(document)
        val result = StreamResult(file)
        transformer.transform(source, result)
        println("Manifest file has been successfully modified and saved.")
    }

    fun extractFileFromApk(apkPath: String, entryName: String, outputPath: String) {
        ZFile.openReadOnly(File(apkPath)).use { zFile ->
            println("APK: " + zFile.entries())
            val entry = zFile.get(entryName)
            if (entry != null) {
                entry.open().use { input ->
                    FileOutputStream(outputPath).use { output ->
                        input.copyTo(output)
                    }
                }
            } else {
                println("No entries found: $entryName")
            }
        }
    }

    fun replaceFileInApk(apkPath: String, entryName: String, newFilePath: String) {
        ZFile.openReadWrite(File(apkPath)).use { zFile ->
            val entry = zFile.get(entryName)
            if (entry != null) {
                entry.delete()
                val newFile = File(newFilePath)
                if (!newFile.exists()) {
                    throw FileNotFoundException("New file does not exist: $newFilePath")
                }
                zFile.add(entryName, newFile.inputStream())
            } else {
                val newFile = File(newFilePath)
                if (!newFile.exists()) {
                    throw FileNotFoundException("New file does not exist: $newFilePath")
                }
                zFile.add(entryName, newFile.inputStream())
            }
        }
    }

    fun addFolderToApk(apkPath: String, entryPath: String, folderPath: String) {
        ZFile.openReadWrite(File(apkPath)).use { zFile ->
            val folder = File(folderPath)
            if (folder.isDirectory) {
                folder.walk().forEach { file ->
                    if (file.isFile) {
                        val relativePath = folder.toURI().relativize(file.toURI()).path
                        val entryName = "$entryPath/$relativePath"
                        zFile.add(entryName, file.inputStream())
                    }
                }
            } else {
                println("$folderPath Not a valid directory")
            }
        }
    }

    fun countDexFiles(apkFilePath: String): Int {
        var dexCount = 0

        try {
            FileInputStream(apkFilePath).use { fis ->
                ZipInputStream(fis).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        if (!entry.isDirectory && entry.name.endsWith(".dex")) {
                            dexCount++
                        }
                        entry = zis.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return dexCount
    }

    fun decodeAXml(inputPath: String, outputPath: String) {
        val axmlParser = AXMLParser()
        axmlParser.parseToXML(inputPath, outputPath)
    }
}

expect fun Apk.encoderAXml(inputPath: String, outputPath: String)
