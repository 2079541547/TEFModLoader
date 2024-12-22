/*******************************************************************************
 * 文件名称: EFData
 * 项目名称: TEFModLoader-Compose
 * 创建时间: 2024/11/17 上午10:58
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
package silkways.terraria.efmodloader.logic.efmod

import android.graphics.Bitmap

data class Mod(
    val info: ModInfo,
    val filePath: String,
    var icon: Bitmap? = null,
    var isEnabled: Boolean = false
) {
    data class ModInfo(
        val name: String,
        val author: String,
        val version: String,
        val introduce: String,
        val github: GithubInfo,
        val mod: ModDetails
    )

    data class GithubInfo(
        val openSource: Boolean,
        val overview: String,
        val url: String
    )

    data class ModDetails(
        val Modx: Boolean,
        val privateData: Boolean,
        val page: Boolean,
        val platform: PlatformSupport
    )

    data class PlatformSupport(
        val Windows: PlatformArchitectures,
        val Android: PlatformArchitectures
    )

    data class PlatformArchitectures(
        val arm64: Boolean,
        val arm32: Boolean,
        val x86_64: Boolean,
        val x86: Boolean
    )
}