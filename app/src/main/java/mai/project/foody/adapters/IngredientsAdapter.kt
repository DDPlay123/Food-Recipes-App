package mai.project.foody.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import mai.project.foody.databinding.IngredientsRowLayoutBinding
import mai.project.foody.models.ExtendedIngredient
import mai.project.foody.util.RecipesDiffUtil

class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    private var ingredients = emptyList<ExtendedIngredient>()

    class IngredientsViewHolder(
        private val binding: IngredientsRowLayoutBinding
    ): ViewHolder(binding.root) {
        fun bind(ingredient: ExtendedIngredient) {
            binding.ingredient = ingredient
            binding.executePendingBindings() // 更新UI
        }
        companion object {
            fun from(parent: ViewGroup): IngredientsViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = IngredientsRowLayoutBinding.inflate(layoutInflater, parent, false)
                return IngredientsViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder =
        IngredientsViewHolder.from(parent)

    override fun getItemCount(): Int = ingredients.size

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        val currentIngredient = ingredients[position]
        holder.bind(currentIngredient)
    }

    fun setData(newIngredients: List<ExtendedIngredient>) {
        val ingredientsDiffUtil = RecipesDiffUtil(ingredients, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredients = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }
}