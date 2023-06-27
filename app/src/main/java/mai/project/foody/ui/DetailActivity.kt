package mai.project.foody.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import mai.project.foody.R
import mai.project.foody.adapters.Pager2Adapter
import mai.project.foody.data.database.entites.FavoritesEntity
import mai.project.foody.databinding.ActivityDetailBinding
import mai.project.foody.ui.fragments.ingredients.IngredientsFragment
import mai.project.foody.ui.fragments.instructions.InstructionsFragment
import mai.project.foody.ui.fragments.overview.OverviewFragment
import mai.project.foody.util.Constants
import mai.project.foody.util.Method
import mai.project.foody.util.showSnackBar
import mai.project.foody.viewmodels.MainViewModel
import java.lang.Exception

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val args by navArgs<DetailActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    private lateinit var menuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        setActionBar()
        doInitialize()
    }

    override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem, R.color.white)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        menuItem = menu!!.findItem(R.id.menu_save_to_favorites)
        checkSavedRecipes(menuItem)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Toolbar back button
        if (item.itemId == android.R.id.home)
            finish()
        else if (item.itemId == R.id.menu_save_to_favorites && !recipeSaved) {
            saveToFavorites(item)
        } else if (item.itemId == R.id.menu_save_to_favorites && recipeSaved) {
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setActionBar() {
        // Set the Toolbar as the ActionBar
        binding.toolBar.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun doInitialize() {
        binding.apply {
            val fragments = ArrayList<Fragment>()
            fragments.add(OverviewFragment())
            fragments.add(IngredientsFragment())
            fragments.add(InstructionsFragment())

            val titles = ArrayList<String>()
            titles.add(getString(R.string.title_overview))
            titles.add(getString(R.string.title_ingredients))
            titles.add(getString(R.string.title_instructions))

            val resultBundle = Bundle()
            resultBundle.putParcelable(Constants.RECIPE_RESULT_KEY, args.result)

            val adapter = Pager2Adapter(
                fragments,
                resultBundle,
                this@DetailActivity.supportFragmentManager,
                lifecycle
            )

            viewPager2.isUserInputEnabled = false
            viewPager2.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }

    private fun checkSavedRecipes(menuItem: MenuItem) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    mainViewModel.readFavoriteRecipes.observe(this@DetailActivity) { favoritesEntity ->
                        try {
                            for (savedRecipe in favoritesEntity) {
                                if (savedRecipe.result.recipeId == args.result.recipeId) {
                                    changeMenuItemColor(menuItem, R.color.yellow)
                                    savedRecipeId = savedRecipe.id
                                    recipeSaved = true
                                }
                            }
                        } catch (e: Exception) {
                            Method.logE("DetailsActivity", "Error", e)
                        }
                    }
                }
            }
        }
    }

    private fun saveToFavorites(item: MenuItem) {
        // id = 0 because it is auto incremented
        val favoritesEntity = FavoritesEntity(0, args.result)
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        binding.root.showSnackBar(getString(R.string.hint_recipe_saved))
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(savedRecipeId, args.result)
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.colorPrimary)
        binding.root.showSnackBar(getString(R.string.hint_removed_from_favorites))
        recipeSaved = false
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }
}