package mai.project.foody.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import mai.project.foody.data.database.RecipesDatabase
import mai.project.foody.util.Constants

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecipesDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()

    @Provides
    fun provideDao(database: RecipesDatabase) =
        database.recipesDao()
}