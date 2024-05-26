package silkways.terraria.toolbox.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.FragmentMainBinding

/**
 * FragmentMain 是应用的主界面Fragment，包含一个底部导航栏。
 */
class FragmentMain : Fragment() {

    /**
     * 使用 Data Binding 的延迟初始化变量，用于绑定 fragment_main.xml 布局。
     */
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    /**
     * 创建 Fragment 的视图。
     *
     * - 使用 LayoutInflater 来从 XML 布局文件中加载视图。
     * - 初始化绑定对象并返回根视图。
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * 当视图创建完成后调用，用于进一步的初始化工作。
     *
     * - 获取底部导航栏（BottomNavigationView）。
     * - 获取当前 Fragment 的 NavHostFragment。
     * - 获取 NavHostFragment 的 NavController，用于导航操作。
     * - 设置 AppBarConfiguration，指定哪些目的地可以在工具栏上进行滑动切换。
     * - 在 AppCompatActivity 上设置 ActionBar 的导航控制器，实现工具栏与导航之间的交互。
     * - 在底部导航栏上设置导航控制器，实现点击底部选项时的页面切换。
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navView: BottomNavigationView = binding.navView
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController

        // 应用栏配置，指定可以滑动切换的导航项
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, // 主页
                R.id.navigation_toolbox, // 工具箱
                R.id.navigation_manage, // 管理
                R.id.navigation_more // 更多
            )
        )

        // 在 AppCompatActivity 上设置 ActionBar 的导航控制器
        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(navController, appBarConfiguration)

        // 在底部导航栏上设置导航控制器
        navView.setupWithNavController(navController)
    }

    /**
     * 当视图销毁时调用，释放绑定对象以避免内存泄漏。
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
