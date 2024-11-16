package silkways.terraria.efmodloader.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.defaults.NestedNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.rememberNavHostEngine
import silkways.terraria.efmodloader.ui.screen.AboutScreen
import silkways.terraria.efmodloader.ui.screen.NavGraphs
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme
import silkways.terraria.efmodloader.ui.utils.LocalSnackbarHost

/*******************************************************************************
 * 文件名称: AboutActivity
 * 项目名称: TEFModLoader-Compose
 * 创建时间: 2024/11/16 上午9:19
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture゙. All rights reserved.
 * 许可证: This program is free software: you can redistribute it and/or modify
 *         it under the terms of the GNU Affero General Public License as published
 *         by the Free Software Foundation, either version 3 of the License, or
 *         (at your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful,
 *         but WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *         GNU Affero General Public License for more details.
 *
 *         You should have received a copy of the GNU Affero General Public License
 *         along with this program. If not, see <https://www.gnu.org/licenses/>.
 * 描述信息: 本文件为TEFModLoader-Compose项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

class AboutActivity: EFActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)

        setContent {
            TEFModLoaderComposeTheme {
                val navController = rememberNavController()
                val snackBarHostState = remember { SnackbarHostState() }
                val navHostEngine = rememberNavHostEngine(
                    navHostContentAlignment = Alignment.TopCenter,
                    rootDefaultAnimations = RootNavGraphDefaultAnimations(
                        enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                        exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                    ),
                    defaultAnimationsForNestedNavGraph = mapOf(
                        NavGraphs.root to NestedNavGraphDefaultAnimations(
                            enterTransition = { fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) },
                            exitTransition = { fadeOut(animationSpec = tween(300, easing = FastOutSlowInEasing)) }
                        )
                    )
                )

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { paddingValues ->
                    CompositionLocalProvider(LocalSnackbarHost provides snackBarHostState) {
                        DestinationsNavHost(
                            modifier = Modifier.padding(paddingValues),
                            navGraph = NavGraphs.root,
                            navController = navController,
                            engine = navHostEngine
                        )
                        AboutScreen()
                    }
                }
            }
        }
    }
}