package mai.project.foody.ui.fragments.recipes

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import mai.project.foody.viewmodels.MainViewModel
import mai.project.foody.R
import mai.project.foody.adapters.RecipesAdapter
import mai.project.foody.databinding.FragmentRecipesBinding
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.Constants
import mai.project.foody.util.NetworkResult
import mai.project.foody.viewmodels.RecipesViewModel

@AndroidEntryPoint
class RecipesFragment : BaseFragment<FragmentRecipesBinding>(R.layout.fragment_recipes) {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }

    override fun FragmentRecipesBinding.initialize() {
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doInitialize()
    }

    private fun doInitialize() {
        setupRecyclerView()
        requestAPIData()
    }

    private fun setupRecyclerView() {
        binding.rvShimmer.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            showShimmer()
        }
    }

    private fun requestAPIData() {
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.rvShimmer.hideShimmer()
                    response.data?.let { recipesAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    binding.rvShimmer.hideShimmer()
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    binding.rvShimmer.showShimmer()
                }
            }
        }
    }
}