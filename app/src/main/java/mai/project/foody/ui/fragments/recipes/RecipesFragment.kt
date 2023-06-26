package mai.project.foody.ui.fragments.recipes

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mai.project.foody.viewmodels.MainViewModel
import mai.project.foody.R
import mai.project.foody.adapters.RecipesAdapter
import mai.project.foody.databinding.FragmentRecipesBinding
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.Method
import mai.project.foody.util.NetworkListener
import mai.project.foody.util.NetworkResult
import mai.project.foody.util.observeOnce
import mai.project.foody.viewmodels.RecipesViewModel

@AndroidEntryPoint
class RecipesFragment :
    BaseFragment<FragmentRecipesBinding>(R.layout.fragment_recipes),
    SearchView.OnQueryTextListener{

    private val args by navArgs<RecipesFragmentArgs>()

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    private val recipesAdapter by lazy { RecipesAdapter() }
    private val networkListener by lazy { NetworkListener() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setup ViewModel
        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun FragmentRecipesBinding.initialize() {
        binding.mainViewModel = this@RecipesFragment.mainViewModel

        // Set the Toolbar as the ActionBar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu, menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let {
            searchAPIData(it)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun searchAPIData(searchQuery: String) {
        binding.rvShimmer.showShimmer()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doInitialize()
        setListener()
    }

    private fun doInitialize() {
        setupRecyclerView()
        doObserve()
    }

    private fun setListener() {
        binding.apply {
            fabRecipes.setOnClickListener {
                if (recipesViewModel.networkStatus)
                    findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
                else
                    recipesViewModel.showNetworkStatus()
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

    private fun doObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                // readDatabase or requestAPIData
                launch {
                    // 這裡用 observeOnce() 是要避免呼叫API後，畫面重整時，又重新讀取資料庫的資料
                    mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { database ->
                        if (database.isNotEmpty() && !args.backFromBottomSheet) {
                            Method.logE("RecipesFragment", "readDatabase called.")
                            recipesAdapter.setData(database[0].foodRecipe)
                            binding.rvShimmer.hideShimmer()
                        } else {
                            requestAPIData()
                        }
                    }
                }
                // Search API
                launch {
                    mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner) { response ->
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

                            else -> Unit
                        }
                    }
                }
                // checkNetworkAvailability
                launch {
                    networkListener.checkNetworkAvailability(requireContext())
                        .collect { status ->
                            Method.logE("RecipesFragment", "Network: $status")
                            recipesViewModel.networkStatus = status
                            recipesViewModel.showNetworkStatus()
                        }
                }
                // checkBackOnline
                launch {
                    recipesViewModel.readBackOnline.asLiveData().observe(viewLifecycleOwner) { backOnline ->
                        recipesViewModel.backOnline = backOnline
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

                else -> Unit
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