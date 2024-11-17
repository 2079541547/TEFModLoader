/*******************************************************************************
 * 文件名称: ManagerActivity
 * 项目名称: TEFModLoader-Compose
 * 创建时间: 2024/11/17 上午10:50
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

package silkways.terraria.efmodloader.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import silkways.terraria.efmodloader.logic.ApplicationSettings.isDarkThemeEnabled
import silkways.terraria.efmodloader.ui.screen.EFModManagerScreen
import silkways.terraria.efmodloader.ui.screen.PreviewKernelManager
import silkways.terraria.efmodloader.ui.theme.TEFModLoaderComposeTheme

class EFManagerActivity: EFActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        init()
        super.onCreate(savedInstanceState)
        setContent {
            TEFModLoaderComposeTheme(darkTheme = isDarkThemeEnabled(this)) {
                if (intent.getBooleanExtra("isMod", true)) {
                    EFModManagerScreen()
                } else {
                    PreviewKernelManager()
                }
            }
        }
    }
}