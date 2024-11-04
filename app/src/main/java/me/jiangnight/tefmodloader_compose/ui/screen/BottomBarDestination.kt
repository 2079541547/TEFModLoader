package me.jiangnight.tefmodloader_compose.ui.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import me.jiangnight.tefmodloader_compose.R
import me.jiangnight.tefmodloader_compose.ui.screen.destinations.HomeScreenDestination
import me.jiangnight.tefmodloader_compose.ui.screen.destinations.ManagerScreenDestination
import me.jiangnight.tefmodloader_compose.ui.screen.destinations.ToolBoxScreenDestination

enum class BottomBarDestination(
    val direction:DirectionDestinationSpec,
    @StringRes val label:Int,
    val iconSelected : ImageVector, //选中图标
    val iconNotSelected : ImageVector, //未选中图标
){
    Home(
        HomeScreenDestination,
        R.string.home,
        Icons.Filled.Home,
        Icons.Outlined.Home
    ),
    ToolBox(
        ToolBoxScreenDestination,
        R.string.toolbox,
        Icons.Filled.Settings,
        Icons.Outlined.Settings
    ),
    Manager(
        ManagerScreenDestination,
        R.string.manager,
        Icons.Filled.Info,
        Icons.Outlined.Info
    )
}

