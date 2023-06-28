package mai.project.foody.ui.fragments.favorites

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import mai.project.foody.R
import mai.project.foody.adapters.FavoriteRecipesAdapter
import mai.project.foody.databinding.FragmentFavoriteRecipesBinding
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.showSnackBar
import mai.project.foody.viewmodels.MainViewModel

@AndroidEntryPoint
class FavoriteRecipesFragment : BaseFragment<FragmentFavoriteRecipesBinding>(R.layout.fragment_favorite_recipes) {

    private val mainViewModel: MainViewModel by viewModels()

    private val favoriteRecipesAdapter by lazy { FavoriteRecipesAdapter(requireActivity(), mainViewModel) }

    override fun FragmentFavoriteRecipesBinding.initialize() {
        binding.mainViewModel = this@FavoriteRecipesFragment.mainViewModel
        binding.mAdapter = favoriteRecipesAdapter

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorite_recipes_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_deleteAll_favorite_recipes) {
                    this@FavoriteRecipesFragment.mainViewModel.deleteAllFavoriteRecipes()
                    binding.root.showSnackBar(getString(R.string.hint_all_recipes_removed))
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun FragmentFavoriteRecipesBinding.destroy() {
        favoriteRecipesAdapter.clearContextualActionMode()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvFavoriteRecipes.apply {
            adapter = favoriteRecipesAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}