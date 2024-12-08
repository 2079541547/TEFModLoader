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

    private lateinit var privateData: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

        privateData = intent.getStringExtra("private").toString()

        setContent {
            TEFModLoaderComposeTheme(darkTheme = isDarkThemeEnabled(this)) {
                loadPluginView()
            }
        }

    }

    @Composable
    private fun loadPluginView() {
        val pluginJarPath = "/storage/emulated/0/Android/data/silkways.terraria.efmodloader/classes.dex"
        val dexClassLoader = loadLocalDex(this, pluginJarPath)
        val klass = dexClassLoader.loadClass("tech.wcw.compose.plugin.PluginV2")
        val obj = klass.newInstance()
        val method = klass.getDeclaredMethod("getPluginView").apply { isAccessible = true }
        (method.invoke(
            this,
            privateData,
            obj) as (@Composable () -> Unit))()
    }

    private fun loadLocalDex(context: Context, pluginDexPath: String): DexClassLoader {
        val dexFile = File(pluginDexPath)
        if (!dexFile.exists()) {
            throw FileNotFoundException("The plugin JAR file does not exist at path: $pluginDexPath")
        }
        val dexOutputDir = File(context.cacheDir, "modPage/dex")
        if (!dexOutputDir.exists()) dexOutputDir.mkdirs()

        return DexClassLoader(
            dexFile.absolutePath,
            dexOutputDir.absolutePath,
            null,
            this.classLoader
        )
    }
}