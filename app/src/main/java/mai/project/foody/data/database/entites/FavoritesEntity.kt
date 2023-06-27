package mai.project.foody.data.database.entites

import androidx.room.Entity
import androidx.room.PrimaryKey
import mai.project.foody.models.Result
import mai.project.foody.util.Constants

@Entity(tableName = Constants.FAVORITE_RECIPES_TABLE)
class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)