package silkways.terraria.efmodloader.ui.fragment.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import silkways.terraria.efmodloader.R
import silkways.terraria.efmodloader.databinding.ManageFragmentEfmodBinding

class EFModFragment: Fragment() {

    private var _binding: ManageFragmentEfmodBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.efmod_manager)

        _binding = ManageFragmentEfmodBinding.inflate(inflater, container, false)



        val recyclerView: RecyclerView = binding.modRecyclerView
        val mods = loadModsFromDirectory("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/EFModData", requireActivity())
        val adapter = ModsAdapter(mods, requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}