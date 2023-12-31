package mai.project.foody.data

import mai.project.foody.data.network.FoodRecipesAPI
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesAPI: FoodRecipesAPI
) {

    suspend fun getRecipes(queries: Map<String, String>) =
        foodRecipesAPI.getRecipes(queries)

    suspend fun searchRecipes(searchQuery: Map<String, String>) =
        foodRecipesAPI.searchRecipes(searchQuery)

    suspend fun getFoodJoke(apiKey: String) =
        foodRecipesAPI.getFoodJoke(apiKey)
}