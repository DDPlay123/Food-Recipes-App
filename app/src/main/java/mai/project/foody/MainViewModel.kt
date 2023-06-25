package mai.project.foody

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import mai.project.foody.data.Repository
import mai.project.foody.data.network.NetworkResult
import mai.project.foody.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    var recipesResponse: MediatorLiveData<NetworkResult<FoodRecipe>> = MediatorLiveData()

    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)
            } catch (e: Exception) {
                e.printStackTrace()
                recipesResponse.value = NetworkResult.Error(getApplication<Application>().getString(R.string.hint_no_recipes))
            }
        } else {
            recipesResponse.value = NetworkResult.Error(getApplication<Application>().getString(R.string.hint_no_internet))
        }
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
        val capibilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capibilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capibilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capibilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}