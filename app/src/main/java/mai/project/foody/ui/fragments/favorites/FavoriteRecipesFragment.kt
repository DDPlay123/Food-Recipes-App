package mai.project.foody.ui.fragments.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mai.project.foody.R
import mai.project.foody.adapters.FavoriteRecipesAdapter
import mai.project.foody.databinding.FragmentFavoriteRecipesBinding
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.viewmodels.MainViewModel

@AndroidEntryPoint
class FavoriteRecipesFragment : BaseFragment<FragmentFavoriteRecipesBinding>(R.layout.fragment_favorite_recipes) {

    private val mainViewModel: MainViewModel by viewModels()

    private val favoriteRecipesAdapter by lazy { FavoriteRecipesAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        doObserve()
    }

    private fun setupRecyclerView() {
        binding.rvFavoriteRecipes.apply {
            adapter = favoriteRecipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun doObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner) { database ->
                        favoriteRecipesAdapter.setData(database)
                    }
                }
            }
        }
    }
}