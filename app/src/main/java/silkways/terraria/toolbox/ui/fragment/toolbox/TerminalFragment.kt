package silkways.terraria.toolbox.ui.fragment.toolbox

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.unity3d.player.UnityPlayerActivity
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.ToolboxFragmentTerminalBinding
import silkways.terraria.toolbox.logic.ApkPatcher
import silkways.terraria.toolbox.logic.JsonConfigModifier
import silkways.terraria.toolbox.logic.ModConfigManager
import silkways.terraria.toolbox.logic.game.AddRes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.log


class TerminalFragment: Fragment() {

    private var _binding: ToolboxFragmentTerminalBinding? = null
    private val binding get() = _binding!!

    private var linesCount = 0
    private val maxLines = 109

    private val commandHistory = mutableListOf<String>()
    private var historyPosition = -1

    private val commandExecutors = mutableMapOf<String, () -> Unit>()

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.title = getString(R.string.terminal)

        _binding = ToolboxFragmentTerminalBinding.inflate(inflater, container, false)

        initializeCommands()

        setupUI()

        return binding.root
    }



    private fun setupUI() {
        with(binding.codeEditor) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    updateLineNumberDisplay()
                }
            })
            setOnEditorActionListener { _, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                    // 确保在按下回车键或完成动作时尝试执行命令
                    val command = text.toString().trim() // 获取并清理命令字符串
                    if (command.isNotBlank()) {
                        executeCommand(command)
                        appendNewLine()
                        moveCursorToEnd()
                    }
                    return@setOnEditorActionListener true
                }
                false
            }
        }
        binding.codeEditor.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                // 获取倒数第二行的命令文本
                binding.codeEditor.append("\n")
                val command = getLastSecondLineCommand()
                if (command.isNotBlank()) {
                    executeCommand(command)
                    appendNewLine()
                    moveCursorToEnd()
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun updateLineNumberDisplay() {
        val currentText = binding.codeEditor.text.toString()
        val lines = currentText.split("\n").take(maxLines) // 确保只计算最多maxLines行
        val formattedLineNumbers = List(lines.size) { index -> (index + 1).toString().padStart(4, ' ') }.joinToString("\n")
        binding.lineNumberContainer.text = formattedLineNumbers
    }

    private fun getLastSecondLineCommand(): String {
        val lines = binding.codeEditor.text.toString().split("\n")
        return if (lines.size > 1) lines[lines.size - 2].trim() else "" // 确保至少有两行才能获取倒数第二行
    }

    // 执行命令逻辑
    private fun executeCommand(command: String) {
        commandHistory.add(command)
        historyPosition = commandHistory.size

        // 确保在添加新行之前检查是否达到最大行数
        if (linesCount + 2 > maxLines) {
            clearScreen() // 清空屏幕内容
        } else {
            linesCount += 2
        }

        val executor = commandExecutors[command] ?: commandExecutors.entries.find { it.key.equals(command, ignoreCase = true) }?.value
        val output = executor?.invoke()?.toString() ?: "Unrecognized command: $command"
        binding.codeEditor.append("$output \n")
        updateLineNumberDisplay() // 更新行号显示
    }




    private fun appendNewLine() {
        binding.codeEditor.append("\n")
        linesCount++
    }

    private fun moveCursorToEnd() {
        binding.codeEditor.setSelection(binding.codeEditor.text.length)
    }



    //////////////////////////////////////////////////////////////////////////////////////////
    //
    //                                      命令执行逻辑
    //
    //////////////////////////////////////////////////////////////////////////////////////////

    private fun initializeCommands() {
        commandExecutors["help"] = { printHelp() }
        commandExecutors["clear"] = { clearScreen() }
        commandExecutors["start"] = {
            val intent = Intent(requireContext(), UnityPlayerActivity::class.java)
            startActivity(intent)
        }
        commandExecutors["exit"] = { requireActivity().finish() }

        commandExecutors["getName"] = {
            val file = File("${requireActivity().cacheDir}/lspatch/origin/")
            val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
            binding.codeEditor.append(files?.get(0)?.name)
        }
        commandExecutors["add_res"] = {
            val file = File("${requireActivity().cacheDir}/lspatch/origin/")
            val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
            ApkPatcher.addSOofAPK("${requireActivity().getExternalFilesDir(null)}/Resources.apk", "${requireActivity().getExternalFilesDir(null)}/1.so",  "assets/0.so")
            //ApkPatcher.addSOofAPK("${requireActivity().getExternalFilesDir(null)}/0.apk", "${requireActivity().getExternalFilesDir(null)}/1.so", "assets/0.so")
        }

        commandExecutors["add_res2"] = {
            val file = File("${requireActivity().cacheDir}/lspatch/origin/")
            val files = file.listFiles { _, name -> name.endsWith(".apk", ignoreCase = true) }
            copyFileWithOverride("${requireActivity().getExternalFilesDir(null)}/Resources.apk", "${requireActivity().cacheDir}/lspatch/origin/${files[0].name}")
        }
        commandExecutors["json"] = {
        }
        commandExecutors["0"] = {
            File("${requireActivity().getExternalFilesDir(null)}/assets").mkdir()
            File("${requireActivity().getExternalFilesDir(null)}/data/Mod_Data").mkdir()
        }
        commandExecutors["install mod"] = {
            //ModConfigManager.Install_Mod(arrayListOf("${requireActivity().getExternalFilesDir(null)}/MyMod-Name.efmod"), "${requireActivity().getExternalFilesDir(null)}/Mod_Data")
        }
    }

    fun copyFileWithOverride(sourcePath: String, targetPath: String) {
        // 创建源文件和目标文件的File对象
        val sourceFile = File(sourcePath)
        val targetFile = File(targetPath)

        // 检查源文件是否存在
        if (!sourceFile.exists()) {
            throw IllegalArgumentException("Source file does not exist.")
        }

        // 如果目标文件已存在，则删除它以准备覆盖
        if (targetFile.exists()) {
            targetFile.delete()
        }

        try {
            // 使用FileChannel实现文件的复制
            val sourceChannel = FileInputStream(sourceFile).channel
            val targetChannel = FileOutputStream(targetFile).channel

            // 尝试传输所有内容，这将自动覆盖目标文件
            targetChannel.transferFrom(sourceChannel, 0, sourceChannel.size())

            // 关闭通道
            sourceChannel.close()
            targetChannel.close()

        } catch (e: IOException) {
            e.printStackTrace()
            // 处理可能的IO异常
        }
    }



    @SuppressLint("SetTextI18n")
    private fun clearScreen() {
        binding.codeEditor.setText("") // 清空屏幕内容
        linesCount = 0 // 重置行数计数
    }

    private fun printHelp() {
        val helpMessage = """
            Available commands:
            help - Display this help message.
            clear - Clear the terminal screen.
            game - Start the game.
            exit - Exit the application.
        """.trimIndent()
        binding.codeEditor.append(helpMessage)
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
