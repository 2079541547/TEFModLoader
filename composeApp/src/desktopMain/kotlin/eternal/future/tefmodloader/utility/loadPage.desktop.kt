package eternal.future.tefmodloader.utility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.io.File
import java.net.URLClassLoader

@Composable
actual fun LoadAndDisplayPage(
    filePath: File,
    className: String,
    params: Map<String, *>
) {
    val classLoader = remember {
        URLClassLoader(
            arrayOf(filePath.toURI().toURL()),
            File::class.java.classLoader
        ).also {
            println("Loaded module from: $filePath")
        }
    }

    val (pageInstance, contentMethod) = remember {
        val clazz = classLoader.loadClass(className)
        val instance = clazz.getDeclaredConstructor().newInstance()
        val method = clazz.getMethod("Content", Map::class.java)
        instance to method
    }

    contentMethod.invoke(pageInstance, params)

}