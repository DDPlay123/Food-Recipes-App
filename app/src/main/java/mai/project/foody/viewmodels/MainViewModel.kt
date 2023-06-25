package mai.project.foody.viewmodels

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mai.project.foody.R
import mai.project.foody.data.Repository
import mai.project.foody.data.database.RecipesEntity
import mai.project.foody.util.NetworkResult
import mai.project.foody.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    /** ROOM DATABASE */
    val readRecipes: LiveData<List<RecipesEntity>>
        get() = repository.local.readDatabase().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch {
            repository.local.insertRecipes(recipesEntity)
        }

    /** RETROFIT */
    private val _recipesResponse: MediatorLiveData<NetworkResult<FoodRecipe>> = MediatorLiveData()
    val recipesResponse: LiveData<NetworkResult<FoodRecipe>>
        get() = _recipesResponse

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        _recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                _recipesResponse.value = handleFoodRecipesResponse(response)

                recipesResponse.value?.data?.let { foodRecipe ->
                    offlineCacheRecipes(foodRecipe)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _recipesResponse.value = NetworkResult.Error(getApplication<Application>().getString(
                    R.string.hint_no_recipes
                ))
            }
        } else {
            _recipesResponse.value = NetworkResult.Error(getApplication<Application>().getString(R.string.hint_no_internet))
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        return when {
            response.message().toString().contains("timeout") ->
                NetworkResult.Error(getApplication<Application>().getString(R.string.hint_timeout))
            response.code() == 402 ->
                NetworkResult.Error(getApplication<Application>().getString(R.string.hint_api_key_limit))
            response.body()?.results.isNullOrEmpty() ->
                NetworkResult.Error(getApplication<Application>().getString(R.string.hint_no_recipes))
            response.isSuccessful ->
                NetworkResult.Success(response.body())
            else ->
                NetworkResult.Error(response.message())
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<Application>().getSystemService(
            Application.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}