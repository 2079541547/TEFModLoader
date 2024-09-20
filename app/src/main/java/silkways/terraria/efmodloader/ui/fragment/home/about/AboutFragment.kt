package silkways.terraria.efmodloader.ui.fragment.home.about

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.HomeAboutDeveloperDialogBinding
import silkways.terraria.efmodloader.databinding.HomeFragmentAboutBinding


/**
 * 关于页面的片段类，用于展示关于应用的信息。
 */
class AboutFragment: Fragment() {

    private var _binding: HomeFragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.about)


        // 初始化导航选项和导航控制器
        val navHostFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navOptions = NavOptions.Builder()
            // 设置导航动画
            .setEnterAnim(R.anim.fragment_anim_enter)
            .setExitAnim(R.anim.fragment_anim_exit)
            .setPopEnterAnim(R.anim.fragment_anim_enter)
            .setPopExitAnim(R.anim.fragment_anim_exit)
            .build()


        // 使用DataBindingUtil或ViewBinding inflate布局文件
        _binding = HomeFragmentAboutBinding.inflate(inflater, container, false)

        //设置彩蛋
        binding.materialCardView.setOnClickListener { rotateImage(binding.shapeableImageView) }
        binding.materialCardView.setOnLongClickListener {
            binding.shapeableImageView.setImageResource(R.drawable.logo)
            true}


        binding.list.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            // 创建ViewHolder
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.home_about_list_item, parent, false)
                return ViewHolder(view)
            }

            // 绑定数据到ViewHolder
            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                if (holder is ViewHolder) {
                    val (title, info, imageResourceId) = logsItems[position]
                    holder.titleTextView.text = title
                    holder.infoTextView.text = info
                    holder.imageView.setImageResource(imageResourceId)
                }
            }

            // 获取数据列表长度
            private val logsItems = listOf(
                Triple(getString(R.string.developer), getString(R.string.developer_text), R.drawable.twotone_people_24),
                Triple(getString(R.string.Open_source_license), getString(R.string.Open_source_license_text), R.drawable.twotone_assured_workload_24),
                Triple(getString(R.string.Open_source_repository), getString(R.string.Open_source_repository_text), R.drawable.twotone_gite_24),
                Triple(getString(R.string.Special_Thanks), getString(R.string.Special_Thanks_text), R.drawable.twotone_handshake_24),
                Triple(getString(R.string.Future_plans), getString(R.string.Future_plans_text), R.drawable.twotone_rocket_launch_24),
                )

            override fun getItemCount(): Int {
                return logsItems.size
            }

            // 自定义ViewHolder类
            inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val titleTextView: TextView = itemView.findViewById(R.id.about_page_title)
                val infoTextView: TextView = itemView.findViewById(R.id.about_page_text)
                val imageView: ShapeableImageView = itemView.findViewById(R.id.about_page_shapeableImageView) // 添加 ImageView 属性

                init {
                    itemView.setOnClickListener {
                        when (adapterPosition) {
                            //开发者的点击事件
                            0 -> {
                                showMaterialDialog()
                            }

                            //开源许可证
                            1 -> {
                                navHostFragment.navController.navigate(R.id.nanavigation_licence, null, navOptions)
                            }

                            //开源仓库
                            2 ->{
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/2079541547/Terraria-ToolBox"))
                                requireActivity().startActivity(browserIntent)
                            }

                            //特别鸣谢
                            3 -> {
                                navHostFragment.navController.navigate(R.id.nanavigation_SpecialThanks, null, navOptions)
                            }

                            //未来计划
                            4 -> {
                                navHostFragment.navController.navigate(R.id.nanavigation_FuturePlans, null, navOptions)
                            }
                        }
                    }
                }
            }


            private fun showMaterialDialog() {

                var dialogBinding: HomeAboutDeveloperDialogBinding? = HomeAboutDeveloperDialogBinding.inflate(LayoutInflater.from(requireActivity()))

                val builder = MaterialAlertDialogBuilder(requireActivity())
                    .setCancelable(false)
                    .setView(dialogBinding?.root)
                    .setPositiveButton(getString(R.string.close), null)

                val dialog = builder.create().apply {
                    //设置窗口特性
                    window?.let { dialogWindow ->
                        setCanceledOnTouchOutside(false) // 设置触摸对话框外部不可取消
                    }

                    var Click_count = 0
                    dialogBinding?.shapeableImageView2?.setOnClickListener {
                        Click_count++
                        if (Click_count >= 40) {
                            Click_count = 0
                            Toast.makeText(requireActivity(), getString(R.string.developer_Easteregg_5_2), Toast.LENGTH_SHORT).show()
                            dialogBinding?.shapeableImageView2?.animate()
                                ?.alpha(0f)
                                ?.setDuration(500)
                                ?.withEndAction {
                                    dialogBinding?.shapeableImageView2?.visibility = View.GONE
                                    //dialogBinding?.button?.visibility = View.GONE
                                }
                                ?.start()
                            dialogBinding?.text?.text = getString(R.string.developer_Easteregg_5_1)
                            Snackbar.make(it, getString(R.string.developer_Easteregg_5), Snackbar.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                requireActivity().finishAffinity() // 结束所有Activity
                                android.os.Process.killProcess(android.os.Process.myPid()) // 结束进程
                            }, 3000)
                        }else if (Click_count >= 30){
                            Snackbar.make(it, getString(R.string.developer_Easteregg_4_1), Snackbar.LENGTH_SHORT).show()
                            dialogBinding?.text?.text = getString(R.string.developer_Easteregg_4)
                        } else if (Click_count >= 20) {
                            Snackbar.make(it, getString(R.string.developer_Easteregg_3_1), Snackbar.LENGTH_SHORT).show()
                            dialogBinding?.text?.text = getString(R.string.developer_Easteregg_3)
                        } else if (Click_count >= 10) {
                            Snackbar.make(it, getString(R.string.developer_Easteregg_2_1), Snackbar.LENGTH_SHORT).show()
                            dialogBinding?.text?.text = getString(R.string.developer_Easteregg_2)
                        } else if (Click_count >= 5) {
                            Snackbar.make(it, getString(R.string.developer_Easteregg_1_1), Snackbar.LENGTH_SHORT).show()
                            dialogBinding?.text?.text = getString(R.string.developer_Easteregg_1)
                        }
                    }


                    // 设置对话框关闭监听器
                    setOnDismissListener {
                        dialogBinding = null // 毁尸灭迹（不是哥们
                    }
                }

                dialog.show()
            }

        }

        binding.list.layoutManager = LinearLayoutManager(requireActivity())

        return binding.root
    }















    /**
     * 使应用图标旋转45度的小彩蛋功能。
     * 每次调用此方法时，应用图标会顺时针旋转45度，
     * 并通过动画实现这一过程。
     */
    private var currentRotation = 0f // 默认角度为0度

    /**
     * 旋转指定的ShapeableImageView。
     *
     * @param view 要旋转的视图（在这里应该是ShapeableImageView）。
     */
    private fun rotateImage(view: View) {
        // 指定每次旋转的角度为45度
        val degrees = 45

        // 强制类型转换，确保传入的是ShapeableImageView
        val imageView = view as ShapeableImageView

        // 创建一个ObjectAnimator来执行旋转动画
        val animator = ObjectAnimator.ofFloat(
            imageView, // 要操作的视图
            "rotation", // 属性名称，这里是要改变的旋转角度
            currentRotation, // 开始角度
            currentRotation + degrees // 结束角度
        )

        // 设置动画持续时间为1秒
        animator.duration = 1000

        // 开始动画
        animator.start()

        // 更新当前旋转角度
        currentRotation += degrees.toFloat()

        // 如果旋转角度超过360度，则重置为0度
        currentRotation %= 360f
    }



    /**
     * 当视图被销毁时调用，释放资源以避免内存泄漏。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // 清理绑定引用，帮助系统回收资源
        _binding = null
    }
}

