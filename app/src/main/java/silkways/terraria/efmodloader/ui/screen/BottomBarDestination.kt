package silkways.terraria.efmodloader.ui.screen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Api
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.screen.destinations.HomeScreenDestination
import silkways.terraria.efmodloader.ui.screen.destinations.ManagerScreenDestination
import silkways.terraria.efmodloader.ui.screen.destinations.ToolBoxScreenDestination
import silkways.terraria.efmodloader.ui.utils.LanguageUtils
import silkways.terraria.efmodloader.utils.SPUtils

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    var label: String,
    val iconSelected: ImageVector,
    val iconNotSelected: ImageVector
) {
    @SuppressLint("StaticFieldLeak")
    Home(
        HomeScreenDestination,
        "", // 初始为空，稍后将被初始化
        Icons.Filled.Home,
        Icons.Outlined.Home
    ),
    @SuppressLint("StaticFieldLeak")
    ToolBox(
        ToolBoxScreenDestination,
        "", // 初始为空，稍后将被初始化
        Icons.Filled.Build,
        Icons.Outlined.Build
    ),
    @SuppressLint("StaticFieldLeak")
    Manager(
        ManagerScreenDestination,
        "", // 初始为空，稍后将被初始化
        Icons.Filled.Api,
        Icons.Outlined.Api
    );

    @SuppressLint("StaticFieldLeak")
    companion object {
        private var context: Context? = null
        private var jsonUtils: LanguageUtils? = null

        fun init(context: Context) {
            this.context = context
            jsonUtils = LanguageUtils(context, LanguageHelper.getLanguage(SPUtils.readInt(silkways.terraria.efmodloader.data.Settings.languageKey, 0), context), "main")
            // 填充枚举的 label
            for (destination in BottomBarDestination.entries) {
                try {
                    when (destination) {
                        Home -> destination.label = jsonUtils!!.getString("home", "title")

                        ToolBox -> destination.label = jsonUtils!!.getString("toolbox", "title")

                        Manager -> destination.label = jsonUtils!!.getString("manager", "title")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}