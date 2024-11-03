package silkways.terraria.efmodloader.logic.efmod

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import org.json.JSONObject
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.logic.EFLog
import java.io.File
import java.io.FileWriter
import java.io.IOException

/*******************************************************************************
 * 文件名称: Utils
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 上午8:01
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

object Utils {

    /**
     * 显示确认对话框，询问用户是否确认某个操作。
     *
     * @param context 应用上下文
     * @param message 要显示的消息
     * @param onConfirm 用户点击确定或取消按钮后的回调函数
     */
    fun showConfirmationDialog(context: Context, message: String, onConfirm: (Boolean) -> Unit) {
        try {
            // 创建对话框构建器
            val dialogBuilder = AlertDialog.Builder(context)

            // 设置对话框的消息
            dialogBuilder.setMessage("${context.getString(R.string.file)} $message ${context.getString(R.string.file_text)}")
                .setCancelable(false) // 不允许用户通过点击外部区域取消对话框

            // 设置确定按钮
            dialogBuilder.setPositiveButton(R.string.determine) { _: DialogInterface, _: Int ->
                onConfirm(true)
                EFLog.i("用户确认了操作: $message")
            }

            // 设置取消按钮
            dialogBuilder.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
                dialog.cancel()
                onConfirm(false)
                EFLog.i("用户取消了操作: $message")
            }

            // 创建对话框
            val alert = dialogBuilder.create()

            // 设置对话框标题
            alert.setTitle("${context.getString(R.string.file)}${context.getString(R.string.file_text)}")

            // 显示对话框
            alert.show()
            EFLog.i("显示确认对话框: $message")
        } catch (e: Exception) {
            // 捕获并记录所有异常
            EFLog.e("显示确认对话框时发生错误: ${e.message}")
            e.printStackTrace()
        }
    }


    /**
     * 将JSON对象写入指定文件中。
     *
     * @param jsonObject 要写入的JSON对象
     * @param file 目标文件
     */
    fun writeJsonToFile(jsonObject: JSONObject, file: File) {
        try {
            // 创建FileWriter对象
            val writer = FileWriter(file)

            // 将JSON对象转换为格式化的字符串，并写入文件
            writer.write(jsonObject.toString(4)) // 使用缩进为4个空格的格式化

            // 关闭写入流
            writer.close()

            // 记录成功日志
            EFLog.i("JSON数据已成功写入文件: ${file.absolutePath}")
        } catch (e: IOException) {
            // 捕获并记录异常
            EFLog.e("写入JSON到文件时发生错误: ${file.absolutePath}, 错误信息: ${e.message}")
        }
    }
}