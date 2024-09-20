package silkways.terraria.efmodloader.ui.fragment.main.toolbox.logic

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import silkways.terraria.efmodloader.R
import java.io.File


class FileListAdapter(private val fileItems: List<FileItem>, private val context: Context) :
    RecyclerView.Adapter<FileListAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.toolbox_file_list, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val item = fileItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = fileItems.size

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val fileName: TextView = itemView.findViewById(R.id.textView)
        private val fileIcon: ImageView = itemView.findViewById(R.id.icon)

        fun bind(fileItem: FileItem) {
            fileName.text = fileItem.name
            if (fileItem.isDirectory) {
                fileIcon.setImageResource(R.drawable.twotone_folder_open_24)
            } else {
                when {
                    fileItem.name.endsWith(".txt", true) || fileItem.name.endsWith(".json", true) -> {
                        fileIcon.setImageResource(R.drawable.twotone_help_24)
                    }
                    else -> {
                        fileIcon.setImageResource(R.drawable.twotone_help_24)
                    }
                }
            }

            itemView.setOnClickListener {
                showPopupMenu(it, fileItem)
            }
        }
    }

    private fun showPopupMenu(view: View, fileItem: FileItem) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.toolbox_file_menu, popupMenu.menu)


        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_open_with_other_app -> {
                    shareFile(context, fileItem.path)
                    true
                }
                R.id.action_delete_permanently -> {
                    deletePermanently(fileItem.path)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }



    private fun deletePermanently(path: String) {
        val file = File(path)
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(context, context.getString(R.string.Delete_file_1), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.getString(R.string.Delete_file_2), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.Delete_file_3), Toast.LENGTH_SHORT).show()
        }
    }



    fun shareFile(context: Context, fileName: String) {
        val uri = getUriForFile(context, fileName)
        if (uri != null) {
            Log.i("MainActivity", "URI: $uri")

            // 创建 Intent
            val intent = Intent(Intent.ACTION_EDIT)
            intent.type = "application/octet-stream"
            intent.setDataAndType(uri, "application/octet-stream")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            // 启动 Intent
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Log.e("MainActivity", "没有应用可以处理此 Intent", e)
            }
        } else {
            Log.e("MainActivity", "无法获取文件的 Uri")
        }
    }


    private fun getUriForFile(context: Context, fileName: String): Uri? {
        val externalFilesDir = context.getExternalFilesDir(null)
        if (externalFilesDir != null) {
            val file = File(fileName)
            if (file.exists()) {
                Log.i("MainActivity", "文件存在: ${file.absolutePath}")
                return FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                Log.i("MainActivity", "文件不存在: ${file.absolutePath}")
                return null
            }
        } else {
            Log.i("MainActivity", "无法获取外部文件目录")
            return null
        }
    }
}