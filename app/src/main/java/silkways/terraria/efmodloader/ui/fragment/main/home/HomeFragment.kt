package silkways.terraria.efmodloader.ui.fragment.main.home

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import eternal.future.effsystem.fileSystem
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.HomeDialogLogsBinding
import silkways.terraria.efmodloader.databinding.MainFragmentHomeBinding
import silkways.terraria.efmodloader.logic.JsonConfigModifier
import java.io.File
import java.util.Calendar
import kotlin.random.Random


/**
 * 主页片段类，负责显示主页内容并处理交互。
 */
class HomeFragment: Fragment() {

    // 绑定视图的变量，使用可空类型并在onDestroyView时置为null
    private var _binding: MainFragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var backPressedTime: Long = 0
    private val timeInterval: Long = 2000 // 设置两次按键之间的时间间隔（毫秒）


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

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.home)

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

        deleteDirectory(File("${requireActivity().cacheDir}/runEFMod"))
        File("${requireActivity().cacheDir}").mkdirs()
        binding.greetings.text = getGreeting() //设置问候语

        //设置一言
        val YiYanArray = JsonConfigModifier.getAssetsArray(requireActivity(), "ToolBoxData/yiyan.json", "data")
        val YiYan = YiYanArray!!.get(Random.nextInt(0, YiYanArray.length())).toString()
        binding.aBriefRemark.text = YiYan

        //切换一言
        binding.SwitchRandomly.setOnClickListener { binding.aBriefRemark.text = YiYanArray.get(Random.nextInt(0, YiYanArray.length())).toString() }


        //跳转关于页面
        binding.about.setOnClickListener { navHostFragment.navController.navigate(R.id.navigation_about, null, navOptions) }

        //跳转设置页面
        binding.setting.setOnClickListener { navHostFragment.navController.navigate(R.id.navigation_settings, null, navOptions) }

        //打开反馈页面
        binding.feedback.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/2079541547/Terraria-ToolBox/issues"))
            requireActivity().startActivity(browserIntent)
        }

        //跳转帮助页面
        binding.help.setOnClickListener { navHostFragment.navController.navigate(R.id.navigation_helps, null, navOptions) }

        //显示更新日志弹窗
        binding.UpdateLog.setOnClickListener { showLogsDialog() }


        // 注册 onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (SystemClock.elapsedRealtime() - backPressedTime < timeInterval) {

                    if(JsonConfigModifier.readJsonValue(requireActivity(), silkways.terraria.efmodloader.data.Settings.jsonPath, silkways.terraria.efmodloader.data.Settings.CleanDialog) as Boolean &&
                        !(JsonConfigModifier.readJsonValue(requireActivity(), silkways.terraria.efmodloader.data.Settings.jsonPath, silkways.terraria.efmodloader.data.Settings.autoClean) as Boolean)
                        ){
                        showCleanDialog()
                    } else if(JsonConfigModifier.readJsonValue(requireActivity(), silkways.terraria.efmodloader.data.Settings.jsonPath, silkways.terraria.efmodloader.data.Settings.autoClean) as Boolean){
                        clearCache()
                        activity?.finishAffinity()
                    } else {
                        activity?.finishAffinity()
                    }
                } else {
                    backPressedTime = SystemClock.elapsedRealtime()
                    Snackbar.make(binding.root, getString(R.string.onBackPressedDispatcher_exit), Snackbar.LENGTH_SHORT).show()
                }
            }
        })

        return binding.root
    }


    //获取问候语
    private fun getGreeting(): String {
        val calendar = Calendar.getInstance() // 获取当前时间
        val hour = calendar.get(Calendar.HOUR_OF_DAY) // 获取小时数

        return when (hour) {
            in 5 until 12 -> getString(R.string.greetings_1) // 早上
            in 12 until 13 -> getString(R.string.greetings_2) // 中午
            in 13 until 18 -> getString(R.string.greetings_5) // 下午
            in 18 until 22 -> getString(R.string.greetings_3) // 晚上
            else -> getString(R.string.greetings_4)
        }
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
     * 创建一个[MaterialAlertDialogBuilder]，设置不可取消并添加绑定的视图。
     * 通过[MaterialAlertDialogBuilder.create()]创建对话框实例，并应用额外的配置，如背景透明度和触摸外部可取消。
     * 初始化[RecyclerView]，包括适配器和布局管理器。
     * 设置对话框关闭监听器，更新[isDialogShowing]标志位并释放[HomeDialogLogsBinding]对象。
     * 最后，如果未显示对话框，则显示它。
     */
    private fun showLogsDialog() {
        if (isDialogShowing) return

        // 初始化Dialog的绑定对象
        var dialogBinding: HomeDialogLogsBinding? = HomeDialogLogsBinding.inflate(LayoutInflater.from(requireActivity()))

        // 创建对话框构建器
        val builder = MaterialAlertDialogBuilder(requireActivity())
            .setCancelable(false)
            .setView(dialogBinding?.root)

        // 创建并配置对话框
        val dialog = builder.create().apply {
            // 设置对话框窗口属性
            window?.let { dialogWindow ->
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
                    holder.itemView.findViewById<MaterialTextView>(R.id.logs_value).text = title
                    holder.itemView.findViewById<MaterialTextView>(R.id.logs_text).text = info
                }

                // 获取数据列表长度
                val logsItems = listOf(
                    Pair(getString(R.string.logs_title_1), getString(R.string.logs_text_1)),
                    )
                override fun getItemCount(): Int {
                    return logsItems.size
                }
            }
            dialogBinding?.logsRecyclerView?.layoutManager = LinearLayoutManager(requireActivity())
            dialogBinding?.logsRecyclerView?.addItemDecoration(
                DividerItemDecoration(
                    requireActivity(),
                    DividerItemDecoration.VERTICAL
                )
            )
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


    private fun showCleanDialog(){
        val builder = MaterialAlertDialogBuilder(requireActivity())


        builder.setTitle(getString(R.string.Clear_cache_title))
        builder.setMessage(getString(R.string.Clear_cache_message))

        builder.setPositiveButton(getString(R.string.Clear_cache)) { dialog: DialogInterface, _: Int ->
            clearCache()
            dialog.dismiss()
            activity?.finishAffinity()
        }

        builder.setNegativeButton(getString(R.string.NOClear_cache)) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            activity?.finishAffinity()
        }

        val dialog: Dialog = builder.create()
        dialog.show()

    }

    private fun clearCache() {
        // 清除应用的内部缓存目录
        val cacheDir = requireActivity().cacheDir
        // 清除应用的外部缓存目录（如果存在）
        val externalCacheDir = requireActivity().externalCacheDir

        deleteDirectory(cacheDir)
        if (externalCacheDir != null) {
            deleteDirectory(externalCacheDir)
        }
    }


    private fun deleteDirectory(directory: File) {
        if (directory.exists()) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            }
            directory.delete()
        }
    }


    /**
     * 当视图被销毁时，清理绑定以避免内存泄漏。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
