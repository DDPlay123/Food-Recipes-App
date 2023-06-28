package mai.project.foody.data.database.entites

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import mai.project.foody.models.FoodJoke
import mai.project.foody.util.Constants

@Entity(tableName = Constants.FOOD_JOKE_TABLE)
class FoodJokeEntity(
    // @Embedded 用於嵌入實體類，可以將實體類中的屬性嵌入到數據庫表中
    @Embedded
    var foodJoke: FoodJoke
) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}