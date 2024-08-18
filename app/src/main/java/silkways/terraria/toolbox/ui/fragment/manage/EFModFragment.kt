package silkways.terraria.toolbox.ui.fragment.manage

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.toolbox.R
import silkways.terraria.toolbox.databinding.ManageFragmentEfmodBinding
import silkways.terraria.toolbox.ui.fragment.manage.mod.JsonParser
import silkways.terraria.toolbox.ui.fragment.manage.mod.ModAdapter
import silkways.terraria.toolbox.ui.fragment.manage.mod.ModDetail

class EFModFragment: Fragment() {

    private var _binding: ManageFragmentEfmodBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ModAdapter
    private val modList = mutableListOf<ModDetail>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.efmod_manager)

        _binding = ManageFragmentEfmodBinding.inflate(inflater, container, false)


        recyclerView = binding.modRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())

        // 创建适配器实例
        adapter = ModAdapter(modList, requireActivity())

        // 将适配器与 RecyclerView 关联起来
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )

        // 加载数据
        loadModDataFromJson()


        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadModDataFromJson() {
        val parser = JsonParser(requireActivity())
        val mods = parser.parseJson("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/ModData/mod_data.json")
        modList.addAll(mods)
        adapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}