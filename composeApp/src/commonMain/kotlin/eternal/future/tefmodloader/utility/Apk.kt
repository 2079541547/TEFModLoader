package eternal.future.tefmodloader.utility

import com.android.apksig.ApkSigner
import com.android.apksig.KeyConfig
import com.android.apksig.KeyConfig.Jca
import com.android.tools.build.apkzlib.zip.AlignmentRules
import com.android.tools.build.apkzlib.zip.ZFile
import com.android.tools.build.apkzlib.zip.ZFileOptions
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
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
    private var Bypass = true
    private var Mode = 0
    private var Debug = false
    private var OverrideVersion = false

    private val Z_FILE_OPTIONS: ZFileOptions? = ZFileOptions().setAlignmentRule(
        AlignmentRules.compose(
            AlignmentRules.constantForSuffix(".so", 4096),
            AlignmentRules.constantForSuffix("assets/silkrift/original.apk", 4096)
        )
    )

    private fun parseJsonFromStream(inputStream: InputStream): JSONObject {
        return inputStream.bufferedReader().use { reader ->
            JSONObject(reader.readText())
        }
    }

    private fun modifyJsonAndReturnStream(json: JSONObject, modifications: (JSONObject) -> Unit): InputStream {
        modifications(json)
        return ByteArrayInputStream(json.toString().toByteArray())
    }

    fun patch(apkPath: String, outPath: String,
              mode: Int, bypass: Boolean = true,
              debug: Boolean = false, overrideVersion: Boolean = false) {

        Bypass = bypass
        Mode = mode
        Debug = debug
        OverrideVersion = overrideVersion

        val srcApkFile = File(apkPath)
        val tempFile = File.createTempFile("patch", ".apk")

        srcApkFile.copyTo(tempFile, true)

        val json = parseJsonFromStream(javaClass.classLoader.getResourceAsStream("patch/config.json")!!)
        val json_input = modifyJsonAndReturnStream(json) {
            it.put("mode", Mode)
            it.put("bypass", Bypass)
        }

        ZFile.openReadWrite(tempFile, Z_FILE_OPTIONS).use { dstZFile ->
            if (Bypass) {
                dstZFile.add("assets/silkrift/original.apk", srcApkFile.inputStream())

                listOf(
                    "arm64-v8a",
                    "armeabi-v7a",
                    "x86",
                    "x86_64"
                ).forEach { architecture ->
                    dstZFile.add("assets/silkrift/so/$architecture/libsilkrift.so",
                        getZipEntryStream("assets/silkrift/so/$architecture/libsilkrift.so"))
                }
            }
            val dexCount = getDexCount(dstZFile)
            dstZFile.add("classes$dexCount.dex", getZipEntryStream("classes.dex"))
            val manifest_org = File.createTempFile("Manifest_org", ".xml")
            val manifest_new = File.createTempFile("Manifest_new", ".xml")

            manifest_new.delete()
            manifest_org.writeBytes(dstZFile.get("AndroidManifest.xml")?.read()!!)
            decodeAXml(manifest_org.path, manifest_new.path)

            modifyManifest(manifest_new.path)
            manifest_org.delete()
            encoderAXml(manifest_new.path, manifest_org.path)

            manifest_new.delete()
            dstZFile.add("AndroidManifest.xml", manifest_org.inputStream())

            dstZFile.add("assets/config.json", json_input)
        }

        signApk(tempFile.path, outPath)
        tempFile.delete()
    }

    private fun getZipEntryStream(entry: String): InputStream? {
        val fileInput = javaClass.classLoader.getResourceAsStream("patch/Bypass.zip") ?: return null
        val zipInput = ZipInputStream(fileInput)
        return generateSequence { zipInput.nextEntry }
            .firstOrNull { it.name == entry }
            ?.let { zipInput }
    }

    private fun getDexCount(zFile: ZFile): Int {
        return zFile.entries()
            .count { entry ->
                val name = entry.centralDirectoryHeader.name
                name.startsWith("classes") && name.endsWith(".dex")
            } + 1
    }

    fun modifyManifest(filePath: String) {
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

        fun addPermission(permissionName: String) {
            val permissionElement = document.createElement("uses-permission").apply {
                setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", permissionName)
            }
            manifestNode.appendChild(permissionElement)
            println("Added permission: $permissionName")
        }

        if (Mode == 0) {
            addPermission("android.permission.READ_EXTERNAL_STORAGE")
            addPermission("android.permission.WRITE_EXTERNAL_STORAGE")
            addPermission("android.permission.MANAGE_EXTERNAL_STORAGE")
        }

        val applicationNodeList = manifestNode.getElementsByTagName("application")
        if (applicationNodeList.length == 0) {
            println("No <application> tag found in the manifest.")
            return
        }
        val applicationNode = applicationNodeList.item(0) as Element
        removeOldIntentFilters(applicationNode)

        val newActivityNode = createActivityElement(document, "eternal.future.TEFModLoader")
        applicationNode.appendChild(newActivityNode)

        if (Debug) {
            applicationNode.setAttributeNS("http://schemas.android.com/apk/res/android", "android:debuggable", "true")
            println("Debuggable attribute set.")
        }

        if (OverrideVersion) {
            manifestNode.setAttributeNS("http://schemas.android.com/apk/res/android", "android:versionCode", "1")
            println("Version code updated.")
        }

        val metaData_ID = document.createElement("meta-data").apply {
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", "TEFModLoader")
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:value", Mode.toString())
        }

        val metaData_Bypass = document.createElement("meta-data").apply {
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:name", "Bypass")
            setAttributeNS("http://schemas.android.com/apk/res/android", "android:value",  if (Bypass) "true" else "false")
        }

        applicationNode.appendChild(metaData_Bypass)
        applicationNode.appendChild(metaData_ID)
        println("Meta-data node added.")

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer().apply {
            setOutputProperty(OutputKeys.INDENT, "yes")
            setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        }
        val source = DOMSource(document)
        val result = StreamResult(filePath)
        transformer.transform(source, result)
        println("Manifest file has been successfully modified and saved.")
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
            .setMinSdkVersion(24)
            .setV1SigningEnabled(false)
            .setV2SigningEnabled(true)
            .setV3SigningEnabled(false)
            .setOtherSignersSignaturesPreserved(false)
        apkSigner.build().sign()
    }
}


expect fun Apk.encoderAXml(inputPath: String, outputPath: String)
expect fun Apk.decodeAXml(inputPath: String, outputPath: String)