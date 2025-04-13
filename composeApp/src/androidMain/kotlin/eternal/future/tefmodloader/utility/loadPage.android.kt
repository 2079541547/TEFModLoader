package eternal.future.tefmodloader.utility


import android.content.Context
import androidx.compose.runtime.Composable
import dalvik.system.DexClassLoader
import eternal.future.tefmodloader.MainApplication
import java.io.File
import java.io.FileNotFoundException

object loadDex {
    fun loadLocalDex(context: Context, pluginDexPath: String): DexClassLoader {
        clearPluginCache(context)

        val originalFile = File(pluginDexPath)
        if (!originalFile.exists()) {
            throw FileNotFoundException("The plugin JAR file does not exist at path: $pluginDexPath")
        }

        val tempFileName = "temp_plugin_" + System.currentTimeMillis() + ".jar"
        val cacheFile = File(context.cacheDir, tempFileName)

        originalFile.inputStream().use { input ->
            cacheFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        cacheFile.setReadOnly()

        val dexOutputDir = File(context.cacheDir, "modPage/dex/${System.currentTimeMillis()}")
        dexOutputDir.mkdirs()

        return DexClassLoader(
            cacheFile.absolutePath,
            dexOutputDir.absolutePath,
            null,
            this::class.java.classLoader
        )
    }

    private fun clearPluginCache(context: Context) {
        val cacheDir = context.cacheDir
        val pluginCacheFiles = cacheDir.listFiles { file ->
            file.isFile && (file.name.startsWith("temp_plugin_") || file.isDirectory && file.name.startsWith(
                "modPage"
            ))
        }

        pluginCacheFiles?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
    }

    private fun deleteDirectory(directory: File) {
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
        directory.delete()
    }
}


@Composable
actual fun LoadAndDisplayPage(
    filePath: File,
    className: String,
    params: Map<String, *>
) {

    val dexClassLoader = loadDex.loadLocalDex(MainApplication.getContext(), filePath.absolutePath)
    val klass = dexClassLoader.loadClass(className)
    val obj = klass.newInstance()

    klass.getDeclaredField("params").apply {
        isAccessible = true
        set(obj, params)
    }

    val contentField = klass.getDeclaredField("Content").apply {
        isAccessible = true
    }
    val contentFunction = contentField.get(obj) as (@Composable () -> Unit)
    contentFunction()
}