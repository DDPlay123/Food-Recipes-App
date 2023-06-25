package mai.project.foody.data

import mai.project.foody.data.network.FoodRecipesAPI
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val foodRecipesAPI: FoodRecipesAPI
) {

    suspend fun getRecipes(queries: Map<String, String>) =
        foodRecipesAPI.getRecipes(queries)
}