package mai.project.foody.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import mai.project.foody.data.database.entites.FavoritesEntity
import mai.project.foody.data.database.entites.RecipesEntity

@Database(entities = [RecipesEntity::class, FavoritesEntity::class], version = 2, exportSchema = false)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao
}