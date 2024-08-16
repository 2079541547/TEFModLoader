package silkways.terraria.toolbox.ui.fragment.toolbox

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Filter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.ToolboxFragmentTerminalBinding
import silkways.terraria.toolbox.logic.terminal.CommandParser


class TerminalFragment: Fragment() {

    private var _binding: ToolboxFragmentTerminalBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.terminal)


        _binding = ToolboxFragmentTerminalBinding.inflate(inflater, container, false)


        val commands = resources.getStringArray(R.array.commands_array)
        val textInputLayout = binding.textInputLayout
        val autoCompleteTextView = textInputLayout.editText as AutoCompleteTextView
        autoCompleteTextView.threshold = 1
        val adapter = CommandAdapter(requireActivity(), android.R.layout.simple_list_item_1, commands.toList())
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val command = commands[position]
            Toast.makeText(requireActivity(), "Selected: $command", Toast.LENGTH_SHORT).show()
        }




        var historyOutput = StringBuilder().append(getString(R.string.terminal_Command_Text))

        binding.runCommand.setOnClickListener {
            val command = autoCompleteTextView.text.toString()
            val commandParser = CommandParser(requireActivity())

            if (command == "clear") {
                historyOutput = StringBuilder().append(getString(R.string.terminal_Command_Text))
            } else {
                val executionResult = commandParser.parseAndExecute(command)
                historyOutput.append("\n").append(executionResult)
            }

            binding.textView4.text = historyOutput.toString()
        }



        return binding.root
    }





    class CommandAdapter(context: Context, resource: Int, objects: List<String>) :
        ArrayAdapter<String>(context, resource, objects) {

        private var originalValues: MutableList<String> = objects.toMutableList()

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filterResults = FilterResults()

                    if (constraint.isNullOrEmpty()) {
                        // 如果输入为空，则显示所有命令
                        filterResults.values = originalValues
                        filterResults.count = originalValues.size
                    } else {
                        val results = ArrayList<String>()
                        val input = constraint.toString().lowercase()

                        // 遍历所有命令，查找包含输入字符的命令
                        for (command in originalValues) {
                            if (command.lowercase().contains(input)) {
                                results.add(command)
                            }
                        }

                        filterResults.values = results
                        filterResults.count = results.size
                    }

                    Log.d("CommandAdapter", "Filtered values count: ${filterResults.count}")
                    return filterResults
                }

                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    if (results != null) {
                        if (results.count >= 0) {
                            clear()
                            addAll(results.values as Collection<String>)
                            notifyDataSetChanged()
                        } else {
                            notifyDataSetInvalidated()
                        }
                    }
                }
            }
        }
    }








    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
