package silkways.terraria.efmodloader.ui.fragment.main.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import org.json.JSONArray
import org.json.JSONObject
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.HomeDialogLogsBinding
import silkways.terraria.efmodloader.databinding.MainFragmentHomeBinding
import silkways.terraria.efmodloader.ui.activity.AboutActivity
import silkways.terraria.efmodloader.ui.activity.SettingsActivity
import silkways.terraria.efmodloader.ui.activity.WebActivity
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Calendar
import kotlin.random.Random

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
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.home)


        // 使用绑定来加载布局文件
        _binding = MainFragmentHomeBinding.inflate(inflater, container, false)

        deleteDirectory(File("${requireActivity().cacheDir}/runEFMod"))
        File("${requireActivity().cacheDir}").mkdirs()
        binding.greetings.text = getGreeting() //设置问候语

        // 设置一言
        val YiYanArray = getQuotesArray(requireActivity(), "TEFModLoader/quotes.json", "quotes")
        val YiYan = getRandomQuote(YiYanArray)

        if (YiYan != null) {
            binding.aBriefRemark.text = "${YiYan.optString("text")} - ${YiYan.optString("source")}"
        }

        // 切换一言
        binding.SwitchRandomly.setOnClickListener {
            val newYiYan = getRandomQuote(YiYanArray)
            if (newYiYan != null) {
                binding.aBriefRemark.text = "${newYiYan.optString("text")} - ${newYiYan.optString("source")}"
            }
        }

        binding.setting.setOnClickListener {
            val intent = Intent(requireActivity(), SettingsActivity::class.java)
            requireActivity().startActivity(intent)
        }

        binding.about.setOnClickListener {
            val intent = Intent(requireActivity(), AboutActivity::class.java)
            requireActivity().startActivity(intent)
        }

        //打开反馈页面
        binding.feedback.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/2079541547/Terraria-ToolBox/issues"))
            requireActivity().startActivity(browserIntent)
        }

        binding.help.setOnClickListener {
            val intent = Intent(requireActivity(), WebActivity::class.java)
            intent.putExtra("Title", getString(R.string.help))
            intent.putExtra("webUrl", "Home/Helps")
            startActivity(intent)
        }

        //显示更新日志弹窗
        binding.UpdateLog.setOnClickListener { showLogsDialog() }

        return binding.root
    }

    private fun getQuotesArray(context: Context, filePath: String, key: String): JSONArray? {
        // 用于读取 assets 文件夹中的 JSON 文件
        val jsonString = context.assets.open(filePath).use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        }

        // 解析 JSON 字符串
        val jsonObject = try {
            JSONObject(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        // 获取指定键对应的数组
        return jsonObject.optJSONArray(key)
    }

    private fun getRandomQuote(yiYanArray: JSONArray?): JSONObject? {
        if (yiYanArray == null || yiYanArray.length() == 0) return null
        val random = Random
        val index = random.nextInt(yiYanArray.length())
        return yiYanArray.optJSONObject(index)
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
                    Pair(getString(R.string.logs_title_150), getString(R.string.logs_text_150)),
                    Pair(getString(R.string.logs_title_121), getString(R.string.logs_text_121)),
                    Pair(getString(R.string.logs_title_120), getString(R.string.logs_text_120)),
                    Pair(getString(R.string.logs_title_100), getString(R.string.logs_text_100)),
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
