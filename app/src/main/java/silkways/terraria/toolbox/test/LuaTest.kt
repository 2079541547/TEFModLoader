package silkways.terraria.toolbox.test

import android.content.res.AssetManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.luaj.vm2.LuaValue
import org.luaj.vm2.lib.OneArgFunction
import org.luaj.vm2.lib.jse.JsePlatform
import silkways.terraria.toolbox.R
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.PrintStream

/**
 * LuaTest活动演示了如何从资源文件夹执行Lua脚本，
 * 捕获其输出，并使用Android的Toast进行显示。
 */
class LuaTest : AppCompatActivity() {

    /**
     * 当活动创建时被调用。初始化UI并执行Lua脚本。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 活动创建时执行Lua脚本并捕获输出。
        executeLuaScriptFromAssetsAndCaptureOutput()
    }

    /**
     * 从资源文件夹执行Lua脚本的方法，
     * 捕获标准输出，并以Toast消息形式展示。
     *
     * 此方法设置Lua运行环境，重定向Lua的print函数以捕获输出，
     * 读取Lua脚本文件，加载并执行脚本，最后展示捕获的输出。
     */
    private fun executeLuaScriptFromAssetsAndCaptureOutput() {
        // 初始化包含LuaJ提供的标准库的全局Lua环境。
        val globals = JsePlatform.standardGlobals()

        // 设置一个流来捕获Lua print语句的输出。
        val outputStream = ByteArrayOutputStream()
        val customPrintStream = PrintStream(outputStream)

        // 将Lua的print函数重定向到我们的自定义输出流。
        globals.set("print", object : OneArgFunction() {
            override fun call(arg: LuaValue): LuaValue {
                // 将Lua字符串值打印到我们的自定义流，并返回Lua的NIL值。
                customPrintStream.println(arg.tojstring())
                return LuaValue.NIL
            }
        })

        // 访问应用的资源管理器以读取Lua脚本文件。
        val assetManager: AssetManager = assets

        // 打开并以文本形式读取Lua脚本文件。
        val scriptReader = BufferedReader(InputStreamReader(assetManager.open("lua/test.lua")))
        val scriptLines = scriptReader.readText()

        // 加载Lua脚本到内存中，关联一个名称便于调试。
        val chunk = globals.load(scriptLines, "lua/test.lua")

        // 执行已加载的Lua脚本。
        chunk.call()

        // 从自定义流捕获输出并转换为字符串。
        val capturedOutput = outputStream.toString(Charsets.UTF_8.name())

        // 通过Toast消息展示捕获的Lua脚本输出。
        Toast.makeText(this, "从Lua脚本捕获的输出: $capturedOutput", Toast.LENGTH_LONG).show()
    }
}
