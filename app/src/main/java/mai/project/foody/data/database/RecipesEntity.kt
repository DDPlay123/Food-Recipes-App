package mai.project.foody.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import mai.project.foody.models.FoodRecipe
import mai.project.foody.util.Constants

@Entity(tableName = Constants.RECIPES_TABLE)
class RecipesEntity(
    var foodRecipe: FoodRecipe
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}