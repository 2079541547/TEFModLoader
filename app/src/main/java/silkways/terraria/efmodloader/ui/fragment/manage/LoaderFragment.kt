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
import silkways.terraria.efmodloader.databinding.ManageFragmentLoaderBinding

class LoaderFragment: Fragment() {

    private var _binding: ManageFragmentLoaderBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).setTitle(R.string.Kernel_management)

        _binding = ManageFragmentLoaderBinding.inflate(inflater, container, false)



        val recyclerView: RecyclerView = binding.modRecyclerView
        val mods = loadLoaderFromDirectory("${requireActivity().getExternalFilesDir(null)}/ToolBoxData/EFModLoaderData", requireActivity())
        val adapter = LoaderAdapter(mods, requireActivity())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}