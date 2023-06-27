package mai.project.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import mai.project.foody.data.database.entites.FavoritesEntity
import mai.project.foody.databinding.FavoriteRecipesRowLayoutBinding
import mai.project.foody.util.RecipesDiffUtil

class FavoriteRecipesAdapter: RecyclerView.Adapter<FavoriteRecipesAdapter.FavoriteRecipesViewHolder>() {

    private var favoritesRecipes = emptyList<FavoritesEntity>()

    class FavoriteRecipesViewHolder(
        private val binding: FavoriteRecipesRowLayoutBinding
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
        val currentRecipe = favoritesRecipes[position]
        holder.bind(currentRecipe)
    }

    fun setData(newData: List<FavoritesEntity>) {
        val recipesDiffUtil = RecipesDiffUtil(favoritesRecipes, newData)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        favoritesRecipes = newData
        diffUtilResult.dispatchUpdatesTo(this)
    }
}