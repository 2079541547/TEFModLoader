package silkways.terraria.toolbox.ui.fragment.main.toolbox.logic

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import silkways.terraria.toolbox.R


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

        if (fileItem.isDirectory) {
            popupMenu.menu.findItem(R.id.action_open_with_editor).isVisible = false
        } else {
            popupMenu.menu.findItem(R.id.action_open_with_editor).isVisible =
                fileItem.name.endsWith(".txt", true) || fileItem.name.endsWith(".json", true)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_open_with_editor -> {
                    openWithEditor(fileItem.path)
                    true
                }
                R.id.action_open_with_other_app -> {
                    openWithOtherApp(fileItem.path)
                    true
                }
                R.id.action_move_to_trash -> {
                    moveToTrash(fileItem.path)
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

    private fun openWithEditor(path: String) {
        Toast.makeText(context, "骗你的，我根本没写", Toast.LENGTH_SHORT).show()
    }

    private fun openWithOtherApp(path: String) {
        Toast.makeText(context, "骗你的，我根本没写", Toast.LENGTH_SHORT).show()
    }

    private fun moveToTrash(path: String) {
        Toast.makeText(context, "骗你的，我根本没写", Toast.LENGTH_SHORT).show()
    }

    private fun deletePermanently(path: String) {
        Toast.makeText(context, "骗你的，我根本没写", Toast.LENGTH_SHORT).show()
    }
}