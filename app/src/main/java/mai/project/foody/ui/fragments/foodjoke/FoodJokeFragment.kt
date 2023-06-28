package mai.project.foody.ui.fragments.foodjoke

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mai.project.foody.R
import mai.project.foody.databinding.FragmentFoodJokeBinding
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.Constants
import mai.project.foody.util.Method
import mai.project.foody.util.NetworkResult
import mai.project.foody.viewmodels.MainViewModel

@AndroidEntryPoint
class FoodJokeFragment : BaseFragment<FragmentFoodJokeBinding>(R.layout.fragment_food_joke) {

    private val mainViewModel: MainViewModel by viewModels()

    private var foodJoke = "No Food Joke"

    override fun FragmentFoodJokeBinding.initialize() {
        binding.mainViewModel = this@FoodJokeFragment.mainViewModel

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.food_joke_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.menu_share_food_joke) {
                    val shareIntent = Intent().apply {
                        this.action = Intent.ACTION_SEND
                        this.putExtra(Intent.EXTRA_TEXT, foodJoke)
                        this.type = "text/plain"
                    }
                    startActivity(shareIntent)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doInitialize()
        doObserve()
    }

    private fun doInitialize() {
        mainViewModel.getFoodJoke(Constants.API_KEY)
    }

    private fun doObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    mainViewModel.foodJokeResponse.observe(viewLifecycleOwner) { response ->
                        when (response) {
                            is NetworkResult.Success -> {
                                response.data?.let {
                                    binding.tvFoodJoke.text = it.text
                                    foodJoke = it.text
                                }
                            }

                            is NetworkResult.Error -> {
                                loadDataFromCache()
                                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                            }

                            is NetworkResult.Loading -> {
                                Method.logE("FoodJokeFragment", "Loading")
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    private fun loadDataFromCache() {
        mainViewModel.readFoodJoke.observe(viewLifecycleOwner) { database ->
            if (!database.isNullOrEmpty()) {
                binding.tvFoodJoke.text = database.first().foodJoke.text
                foodJoke = database.first().foodJoke.text
            }
        }
    }
}