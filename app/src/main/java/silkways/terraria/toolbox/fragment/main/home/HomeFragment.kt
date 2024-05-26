package silkways.terraria.toolbox.fragment.main.home

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.HomeDialogLogsBinding
import silkways.terraria.toolbox.databinding.MainFragmentHomeBinding

/**
 * 主页片段类，负责显示主页内容并处理交互。
 */
class HomeFragment: Fragment() {

    // 绑定视图的变量，使用可空类型并在onDestroyView时置为null
    private var _binding: MainFragmentHomeBinding? = null
    private val binding get() = _binding!!

    /**
     * 创建视图。
     *
     * @param inflater 布局填充器，用于将XML布局文件转换为视图对象。
     * @param container 可选的视图容器，如果存在，用于插入新创建的视图。
     * @param savedInstanceState 如果当前片段之前已存在，保存的实例状态。
     * @return 返回这个片段的主视图。
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 初始化导航选项和导航控制器
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navOptions = NavOptions.Builder()
            // 设置导航动画
            .setEnterAnim(R.anim.fragment_anim_enter)
            .setExitAnim(R.anim.fragment_anim_exit)
            .setPopEnterAnim(R.anim.fragment_anim_enter)
            .setPopExitAnim(R.anim.fragment_anim_exit)
            .build()




        // 使用绑定来加载布局文件
        _binding = MainFragmentHomeBinding.inflate(inflater, container, false)


        // 设置点击事件
        // 点击游戏按钮时的逻辑（未指定具体导航目标）
        binding.game.setOnClickListener { /* 导航到游戏片段 */ }
        // 点击关于按钮时导航到关于页面
        binding.about.setOnClickListener {
            navHostFragment.navController.navigate(
                R.id.navigation_about,
                null,
                navOptions)
        }
        // 点击设置按钮时导航到设置页面
        binding.settings.setOnClickListener {
            navHostFragment.navController.navigate(
                R.id.navigation_settings,
                null,
                navOptions)
        }
        // 点击日志按钮时调用显示更新日志对话框的方法（未实现）
        binding.logs.setOnClickListener { showLogsDialog() }
        // 点击技术支持按钮时打开GitHub页面
        binding.techSupport.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/2079541547/Terraria_ToolBox")
                )
            )
        }

        // 设置作者列表适配器
        val writerItems = listOf(
            // 每个条目包含标题、信息和图像资源
            Triple(
                "EternalFuture゙", resources.getString(R.string.writer_text1), R.drawable.eternalfuture
            ),
        )
        // 创建一个RecyclerView的适配器
        binding.wirter.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            // 创建ViewHolder
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.writer_layout, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            // 绑定数据到ViewHolder
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val (title, info, imageResource) = writerItems[position]
                // 设置ViewHolder中的文本和图像
                holder.itemView.findViewById<TextView>(R.id.writer_name).text = title
                holder.itemView.findViewById<TextView>(R.id.writer_trcy).text = info
                holder.itemView.findViewById<ShapeableImageView>(R.id.writer_image).setImageResource(imageResource)
            }

            // 获取列表项数量
            override fun getItemCount(): Int {
                return writerItems.size
            }
        }
        // 设置RecyclerView的布局管理器
        binding.wirter.layoutManager = LinearLayoutManager(requireActivity())

        // 返回根视图
        return binding.root
    }

    /**
     * 是否正在显示日志对话框的标志位
     */
    private var isDialogShowing = false

    /**
     * 显示日志对话框的方法
     *
     * 检查对话框是否已显示，如果已显示则直接返回。
     * 使用[LayoutInflater]从布局文件中创建[HomeDialogLogsBinding]对象。
     * 创建一个[AlertDialog.Builder]，设置不可取消并添加绑定的视图。
     * 通过[AlertDialog.Builder.create()]创建对话框实例，并应用额外的配置，如背景透明度和触摸外部可取消。
     * 初始化[RecyclerView]，包括适配器和布局管理器。
     * 设置对话框关闭监听器，更新[isDialogShowing]标志位并释放[HomeDialogLogsBinding]对象。
     * 最后，如果未显示对话框，则显示它。
     */
    private fun showLogsDialog() {
        if (isDialogShowing) return

        // 初始化Dialog的绑定对象
        var dialogBinding: HomeDialogLogsBinding? = HomeDialogLogsBinding.inflate(LayoutInflater.from(requireActivity()))

        // 创建对话框构建器
        val builder = AlertDialog.Builder(requireActivity())
            .setCancelable(false)
            .setView(dialogBinding?.root)

        // 创建并配置对话框
        val dialog = builder.create().apply {
            // 设置对话框窗口属性
            window?.let { dialogWindow ->
                dialogWindow.setBackgroundDrawable(ColorDrawable(0x00000000)) // 设置背景透明
                setCanceledOnTouchOutside(true) // 设置触摸对话框外部可取消
            }

            // 初始化RecyclerView
            dialogBinding?.logsRecyclerView?.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                // 创建ViewHolder
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.home_logs_item_layout, parent, false)
                    return object : RecyclerView.ViewHolder(view) {}
                }

                // 绑定数据到ViewHolder
                override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                    val (title, info) = logsItems[position]
                    holder.itemView.findViewById<TextView>(R.id.logs_value).text = title
                    holder.itemView.findViewById<TextView>(R.id.logs_text).text = info
                }

                // 获取数据列表长度
                val logsItems = listOf(
                    Pair(getString(R.string.logs_title_1), getString(R.string.logs_text_1))
                )
                override fun getItemCount(): Int {
                    return logsItems.size
                }
            }
            dialogBinding?.logsRecyclerView?.layoutManager = LinearLayoutManager(requireActivity())

            // 设置对话框关闭监听器
            setOnDismissListener {
                isDialogShowing = false
                dialogBinding = null
            }
        }

        // 如果对话框未显示，显示它
        isDialogShowing = true
        dialog.show()
    }

    /**
     * 当视图被销毁时，清理绑定以避免内存泄漏。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
