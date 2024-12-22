package silkways.terraria.efmodloader.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import dalvik.system.DexClassLoader
import silkways.terraria.efmodloader.logic.ApplicationSettings.isDarkThemeEnabled
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import java.io.File
import java.io.FileNotFoundException

class ModPage : EFActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        val path = intent.getStringExtra("page").toString()

        setContent {
            TEFModLoaderComposeTheme(darkTheme = isDarkThemeEnabled(this)) {
                LoadPluginView(path, File(path).parent)
            }
        }

    }

    @Composable
    private fun LoadPluginView(pluginJarPath: String, Data: String) {
        val dexClassLoader = loadLocalDex(this, pluginJarPath)
        val klass = dexClassLoader.loadClass("eternalfuture.efmod.page")
        val obj = klass.newInstance()

        klass.getDeclaredField("private").apply {
            isAccessible = true
            set(obj, Data)
        }

        klass.getDeclaredField("platform").apply {
            isAccessible = true
            set(obj, "Android")
        }

        val method = klass.getDeclaredMethod("getModView").apply { isAccessible = true }
        (method.invoke(obj) as (@Composable () -> Unit))()
    }

    private fun loadLocalDex(context: Context, pluginDexPath: String): DexClassLoader {
        // 清除旧的插件缓存
        clearPluginCache(context)

        val originalFile = File(pluginDexPath)
        if (!originalFile.exists()) {
            throw FileNotFoundException("The plugin JAR file does not exist at path: $pluginDexPath")
        }

        // 创建一个临时文件名，并确保它是只读的
        val tempFileName = "temp_plugin_" + System.currentTimeMillis() + ".jar"
        val cacheFile = File(context.cacheDir, tempFileName)

        // 将原始文件复制到缓存目录
        originalFile.inputStream().use { input ->
            cacheFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        // 设置文件为只读
        cacheFile.setReadOnly()

        // 使用新的 dex 输出目录
        val dexOutputDir = File(context.cacheDir, "modPage/dex/${System.currentTimeMillis()}")
        dexOutputDir.mkdirs()

        return DexClassLoader(
            cacheFile.absolutePath,
            dexOutputDir.absolutePath,
            null,
            this.classLoader
        )
    }

    /**
     * 清除缓存目录中的所有插件相关文件。
     */
    private fun clearPluginCache(context: Context) {
        val cacheDir = context.cacheDir
        val pluginCacheFiles = cacheDir.listFiles { file ->
            file.isFile && (file.name.startsWith("temp_plugin_") || file.isDirectory && file.name.startsWith("modPage"))
        }

        pluginCacheFiles?.forEach { file ->
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
    }

    /**
     * 递归删除目录及其内容。
     */
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