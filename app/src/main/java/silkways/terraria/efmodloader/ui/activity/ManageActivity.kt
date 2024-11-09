package silkways.terraria.efmodloader.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import silkways.terraria.efmodloader.data.Settings
import silkways.terraria.efmodloader.databinding.ActivityManageBinding
import silkways.terraria.efmodloader.logic.LanguageHelper
import silkways.terraria.efmodloader.ui.adapter.LoaderAdapter
import silkways.terraria.efmodloader.ui.adapter.ModsAdapter
import silkways.terraria.efmodloader.ui.adapter.loadLoaderFromDirectory
import silkways.terraria.efmodloader.ui.adapter.loadModsFromDirectory
import silkways.terraria.efmodloader.utils.SPUtils

/*******************************************************************************
 * 文件名称: ManageActivity
 * 项目名称: TEFModLoader
 * 创建时间: 2024/11/3 上午9:46
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
 * 描述信息: 本文件为TEFModLoader项目中的一部分。
 * 注意事项: 请严格遵守GNU AGPL v3.0协议使用本代码，任何未经授权的商业用途均属侵权行为。
 *******************************************************************************/

class ManageActivity: AppCompatActivity() {

    private lateinit var binding: ActivityManageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LanguageHelper.setAppLanguage(this, LanguageHelper.getAppLanguage(SPUtils.readInt(Settings.languageKey, 0), this))

        binding = ActivityManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.title = intent.getStringExtra("Title").toString()

        val recyclerView: RecyclerView = binding.manageRecyclerView

        if (intent.getBooleanExtra("isMod", true)) {
            val adapter = ModsAdapter(
                loadModsFromDirectory(
                    "${this.getExternalFilesDir(null)}/TEFModLoader/EFModData",
                    this
                ), this
            )
            recyclerView.adapter = adapter
        } else {
            val adapter = LoaderAdapter(
                loadLoaderFromDirectory(
                    "${this.getExternalFilesDir(null)}/TEFModLoader/EFModLoaderData",
                    this),
                this)

            recyclerView.adapter = adapter
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

    }


}