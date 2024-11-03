package silkways.terraria.efmodloader.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import silkways.terraria.efmodloader.databinding.ActivitySettingBinding
import silkways.terraria.efmodloader.ui.adapter.settings.SettingAdapter
import silkways.terraria.efmodloader.ui.adapter.settings.SettingItem
import kotlin.collections.mutableListOf

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var settings: MutableList<SettingItem>
    private lateinit var adapter: SettingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 RecyclerView
        val recyclerView = binding.SettingsRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 初始化设置列表
        settings = mutableListOf()

        // 创建并设置适配器
        adapter = SettingAdapter(settings, this)
        recyclerView.adapter = adapter


        //addSetting(SettingItem.Title())
    }

    /**
     * 动态添加设置项
     */
    @SuppressLint("NotifyDataSetChanged")
    fun addSetting(vararg settingItems: SettingItem) {
        settings.addAll(settingItems)
        adapter.notifyDataSetChanged()
    }
}