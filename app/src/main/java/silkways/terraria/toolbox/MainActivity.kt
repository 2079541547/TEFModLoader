package silkways.terraria.toolbox

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import silkways.terraria.toolbox.databinding.ActivityMainBinding

/**
 * MainActivity 是应用的主要入口点，继承自 AppCompatActivity。
 * 它使用 Jetpack Navigation 和 Data Binding 库来管理界面导航和视图绑定。
 */
class MainActivity : AppCompatActivity() {

    /**
     * ActivityMainBinding 的一个延迟初始化变量，用于绑定布局文件 activity_main.xml。
     * 这个绑定对象提供了对 UI 组件的直接访问。
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * 当活动创建时调用，负责设置布局和初始化必要的组件。
     *
     * - 使用 `inflate` 方法将 activity_main.xml 布局文件加载到 layoutInflater 中。
     * - 隐藏状态栏和导航栏，实现全屏显示。
     * - 将加载后的布局设置为活动的内容视图。
     * - 获取 NavHostFragment 并将其 navController 用于导航操作。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 初始化布局绑定
        binding = ActivityMainBinding.inflate(layoutInflater)

        // 设置全屏模式
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        // 设置内容视图
        setContentView(binding.root)

        // 获取 NavHostFragment，它是 Jetpack Navigation 的核心组件
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment

        // 获取 NavHostFragment 内的 NavController，用于控制界面间的导航
        navHostFragment.navController
    }
}
