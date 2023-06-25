package mai.project.foody.ui.fragments.recipes

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mai.project.foody.viewmodels.MainViewModel
import mai.project.foody.R
import mai.project.foody.adapters.RecipesAdapter
import mai.project.foody.databinding.FragmentRecipesBinding
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.Method
import mai.project.foody.util.NetworkResult
import mai.project.foody.util.observeOnce
import mai.project.foody.viewmodels.RecipesViewModel

@AndroidEntryPoint
class RecipesFragment : BaseFragment<FragmentRecipesBinding>(R.layout.fragment_recipes) {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun FragmentRecipesBinding.initialize() {
        binding.mainViewModel = this@RecipesFragment.mainViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doInitialize()
        setListener()
    }

    private fun doInitialize() {
        setupRecyclerView()
        readDatabase()
    }

    private fun setListener() {
        binding.apply {
            fabRecipes.setOnClickListener {
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvShimmer.apply {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            showShimmer()
        }
    }

    private fun readDatabase() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    // 這裡用 observeOnce() 是要避免呼叫API後，畫面重整時，又重新讀取資料庫的資料
                    mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                        if (database.isNotEmpty()) {
                            Method.logE("RecipesFragment", "readDatabase called.")
                            recipesAdapter.setData(database[0].foodRecipe)
                            binding.rvShimmer.hideShimmer()
                        } else {
                            requestAPIData()
                        }
                    }
                }
            }
        }
    }

    private fun requestAPIData() {
        Method.logE("RecipesFragment", "requestAPIData called.")
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.rvShimmer.hideShimmer()
                    response.data?.let { recipesAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    binding.rvShimmer.hideShimmer()
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Loading -> {
                    binding.rvShimmer.showShimmer()
                }
            }
        }
    }

    private fun loadDataFromCache() {
        mainViewModel.readRecipes.observe(viewLifecycleOwner) { database ->
            if (database.isNotEmpty()) {
                Method.logE("RecipesFragment", "loadDataFromCache called.")
                recipesAdapter.setData(database[0].foodRecipe)
            }
        }
    }
}