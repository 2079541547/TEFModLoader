package silkways.terraria.efmodloader.ui.activity

import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.ActivityTerminalBinding
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.logic.terminal.CommandParser
import silkways.terraria.efmodloader.ui.adapter.CommandAdapter
import silkways.terraria.efmodloader.utils.SPUtils

/*******************************************************************************
 * 文件名称: TerminalActivity
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 上午10:29
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

class TerminalActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTerminalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setAppLanguage(this, LanguageHelper.getLanguage(SPUtils.readInt(Settings.languageKey, 0), this))

        binding = ActivityTerminalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val commands = resources.getStringArray(R.array.commands_array)
        val textInputLayout = binding.textInputLayout
        val autoCompleteTextView = textInputLayout.editText as AutoCompleteTextView
        autoCompleteTextView.threshold = 1
        val adapter = CommandAdapter(this, android.R.layout.simple_list_item_1, commands.toList())
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val command = commands[position]
            Toast.makeText(this, "Selected: $command", Toast.LENGTH_SHORT).show()
        }

        var historyOutput = StringBuilder().append(getString(R.string.terminal_Command_Text))

        binding.runCommand.setOnClickListener {
            val command = autoCompleteTextView.text.toString()
            val commandParser = CommandParser(this)

            if (command == "clear") {
                historyOutput = StringBuilder().append(getString(R.string.terminal_Command_Text))
            } else {
                val executionResult = commandParser.parseAndExecute(command)
                historyOutput.append("\n").append(executionResult)
            }

            binding.textView4.text = historyOutput.toString()
        }
    }

}