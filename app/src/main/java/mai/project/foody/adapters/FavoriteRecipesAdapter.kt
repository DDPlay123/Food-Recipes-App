package mai.project.foody.adapters

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import mai.project.foody.R
import mai.project.foody.data.database.entites.FavoritesEntity
import mai.project.foody.databinding.FavoriteRecipesRowLayoutBinding
import mai.project.foody.ui.fragments.favorites.FavoriteRecipesFragmentDirections
import mai.project.foody.util.RecipesDiffUtil
import mai.project.foody.util.showSnackBar
import mai.project.foody.viewmodels.MainViewModel

class FavoriteRecipesAdapter(
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
): RecyclerView.Adapter<FavoriteRecipesAdapter.FavoriteRecipesViewHolder>(),
    ActionMode.Callback {

    private var multiSelection = false

    private lateinit var mActionMode: ActionMode
    private lateinit var rootView: View

    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var myViewHolders = arrayListOf<FavoriteRecipesViewHolder>()
    private var favoritesRecipes = emptyList<FavoritesEntity>()

    class FavoriteRecipesViewHolder(
        val binding: FavoriteRecipesRowLayoutBinding
    ): ViewHolder(binding.root) {
        fun bind(favoritesEntity: FavoritesEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings() // 更新UI
        }
        companion object {
            fun from(parent: ViewGroup): FavoriteRecipesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return FavoriteRecipesViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipesViewHolder =
        FavoriteRecipesViewHolder.from(parent)

    override fun getItemCount(): Int = favoritesRecipes.size

    override fun onBindViewHolder(holder: FavoriteRecipesViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.binding.root

        val currentRecipe = favoritesRecipes[position]
        holder.bind(currentRecipe)

        holder.binding.root.apply {
            setOnClickListener {
                if (multiSelection) {
                    applySelection(holder, currentRecipe)
                } else {
                    val action = FavoriteRecipesFragmentDirections
                        .actionFavoriteRecipesFragmentToDetailActivity(currentRecipe.result)
                    findNavController().navigate(action)
                }
            }

            setOnLongClickListener {
                if (!multiSelection) {
                    multiSelection = true
                    requireActivity.startActionMode(this@FavoriteRecipesAdapter)
                    applySelection(holder, currentRecipe)
                    true
                } else {
                    applySelection(holder, currentRecipe)
                    true
                }
            }
        }
    }

    private fun applySelection(
        holder: FavoriteRecipesViewHolder,
        currentRecipe: FavoritesEntity
    ) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(
        holder: FavoriteRecipesViewHolder,
        backgroundColor: Int,
        strokeColor: Int
    ) {
        holder.binding.apply {
            root.setBackgroundColor(ContextCompat.getColor(requireActivity, backgroundColor))
            cardFavoriteRecipe.strokeColor = ContextCompat.getColor(requireActivity, strokeColor)
        }
    }

    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
            }
            1 -> {
                mActionMode.title = String.format(requireActivity.getString(R.string.number_item_selected), selectedRecipes.size)
            }
            else -> {
                mActionMode.title = String.format(requireActivity.getString(R.string.number_item_selected), selectedRecipes.size)
            }
        }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        mActionMode = mode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_delete_favorite_recipe) {
            selectedRecipes.forEach { favoritesEntity ->
                mainViewModel.deleteFavoriteRecipe(favoritesEntity)
            }
            rootView.showSnackBar(String.format(requireActivity.getString(R.string.number_recipes_removed), selectedRecipes.size))
            multiSelection = false
            selectedRecipes.clear()
            mode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }

    fun setData(newData: List<FavoritesEntity>) {
        val recipesDiffUtil = RecipesDiffUtil(favoritesRecipes, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        favoritesRecipes = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }

    fun clearContextualActionMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }
    }
}