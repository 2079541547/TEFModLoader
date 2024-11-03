package silkways.terraria.efmodloader.ui.adapter

import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filter.FilterResults

/*******************************************************************************
 * 文件名称: CommandAdapter
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 上午10:36
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