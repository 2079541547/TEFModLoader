package eternalfuture.efmod

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*******************************************************************************
 * 文件名称: page
 * 项目名称: TEFModLoader
 * 创建时间: 2024/12/21
 * 作者: EternalFuture゙
 * Github: https://github.com/2079541547
 * 版权声明: Copyright © 2024 EternalFuture. All rights reserved.
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
 *
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/



class page {

 public var platform: String = ""
 public var private: String = ""

 val ModView: (@Composable () -> Unit) = {
  Box(
   modifier = Modifier.fillMaxWidth().fillMaxHeight(),
   content = {
    Card(
     modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
     elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
     Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
     ) {
      Spacer(modifier = Modifier.width(16.dp))
      Text(
       text = private,
       modifier = Modifier.weight(1f),
       fontSize = 16.sp
      )
      Switch(
       checked = true,
       onCheckedChange = {}
      )
     }
    }
   }
  )
 }
}