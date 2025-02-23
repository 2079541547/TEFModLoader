package eternal.future.efmodloader.utility

import cn.zaratustra.axmlparser.core.AXMLParser
import com.android.apksig.ApkSigner
import com.android.apksig.KeyConfig
import com.android.apksig.KeyConfig.Jca
import com.android.tools.build.apkzlib.zip.ZFile
import eternal.future.efmodloader.State.Debugging
import eternal.future.efmodloader.State.OverrideVersion
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Security
import java.security.cert.X509Certificate
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

object Apk {
    fun modifyManifest(filePath: String, mode: Int = 0, addDebuggable: Boolean, updateVersionCode: Boolean, packName: String = "") {
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

        manifestNode.setAttributeNS("http://schemas.android.com/apk/res/android", "android:sharedUserId", "future.tefmodloader")

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
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:value", mode.toString())
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

    fun signApk(inputPath: String, outputPath: String) {
        val keystorePassword = "TEFModLoader"
        val keyAlias = "EternalFuture"
        val keyPassword = "TEFModLoader"
        val input = File(inputPath)
        val output = File(outputPath)

        Security.addProvider(BouncyCastleProvider())

        val keyStore = KeyStore.getInstance("BKS", BouncyCastleProvider.PROVIDER_NAME)

        keyStore.load(javaClass.classLoader.getResourceAsStream("patch/TEFModLoader.bks"), keystorePassword.toCharArray())

        val privateKey = keyStore.getKey(keyAlias, keyPassword.toCharArray()) as PrivateKey
        val certificateChain = keyStore.getCertificateChain(keyAlias)

        val x509Certificates: MutableList<X509Certificate> = ArrayList()
        for (cert in certificateChain) {
            x509Certificates.add(cert as X509Certificate)
        }

        val keyConfig: KeyConfig = Jca(privateKey)

        val signerConfig = ApkSigner.SignerConfig.Builder("TEFML", keyConfig, x509Certificates).build()

        val apkSigner = ApkSigner.Builder(listOf(signerConfig))
            .setInputApk(input)
            .setOutputApk(output)
            .setV1SigningEnabled(false)
            .setV2SigningEnabled(true)
            .setV3SigningEnabled(false)
            .setOtherSignersSignaturesPreserved(false)

        apkSigner.build().sign()
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

    fun patchGame(mode: Int,
                  apkPath: File,
                  newPackName: String = "",
                  debug: Boolean = false,
                  overrideVersion: Boolean = false
                  ) {
        apkPath.let {
            if (!it.exists()) return

            if (mode != 2 && mode != 3) {
                val axml = File(it.parent, "AndroidManifest.xml")
                val axml_temp = File(it.parent, "AndroidManifest_temp.xml")

                extractFileFromApk(it.path, "AndroidManifest.xml", axml.path)

                decodeAXml(axml.path, axml_temp.path)
                modifyManifest(axml_temp.path, mode, debug, overrideVersion, newPackName)
                axml.delete()
                Apk.encoderAXml(axml_temp.path, axml.path)
                replaceFileInApk(it.path, "AndroidManifest.xml", axml.path)

                val dexc = countDexFiles(it.path) + 1
                javaClass.classLoader?.getResourceAsStream("patch/classes.dex")
                javaClass.classLoader?.getResourceAsStream("patch/config.json")

                val cj = File(it.parent, "config.json")
                val d = File(it.parent, "classes$dexc.dex")

                FileOutputStream(cj).use { fileOutputStream ->
                    javaClass.classLoader?.getResourceAsStream("patch/config.json")?.copyTo(fileOutputStream)
                }

                cj.writeText(JSONObject(cj.readText()).put("mode", mode).toString(4))

                FileOutputStream(d).use { fileOutputStream ->
                    javaClass.classLoader?.getResourceAsStream("patch/classes.dex")?.copyTo(fileOutputStream)
                }

                replaceFileInApk(it.path, d.name, d.path)
                replaceFileInApk(it.path, "assets/config.json", cj.path)

                cj.delete()
                d.delete()

                axml_temp.delete()
                axml.delete()
                signApk(it.path, File(it.parent, "sign.apk").path)
                it.delete()
                File(it.parent, "sign.apk").renameTo(it)
            } else {

            }
        }
    }
}

expect fun Apk.encoderAXml(inputPath: String, outputPath: String)
