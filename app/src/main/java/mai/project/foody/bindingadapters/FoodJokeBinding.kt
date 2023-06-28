package mai.project.foody.bindingadapters

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView
import mai.project.foody.data.database.entites.FoodJokeEntity
import mai.project.foody.models.FoodJoke
import mai.project.foody.util.NetworkResult

class FoodJokeBinding {

    companion object {

        @BindingAdapter("readApiResponse3", "readDatabase3", requireAll = false)
        @JvmStatic
        fun setCardAndProgressVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ) {
            when (apiResponse) {
                is NetworkResult.Loading -> {
                    when (view) {
                        is ProgressBar -> view.visibility = View.VISIBLE

                        is MaterialCardView -> view.visibility = View.INVISIBLE
                    }
                }

                is NetworkResult.Error -> {
                    when (view) {
                        is ProgressBar -> view.visibility = View.INVISIBLE

                        is MaterialCardView -> {
                            view.visibility = View.VISIBLE
                            if (database != null && database.isEmpty())
                                view.visibility = View.INVISIBLE
                        }
                    }
                }

                is NetworkResult.Success -> {
                    when(view){
                        is ProgressBar -> view.visibility = View.INVISIBLE

                        is MaterialCardView -> view.visibility = View.VISIBLE
                    }
                }

                else -> Unit
            }
        }

        @BindingAdapter("readApiResponse4", "readDatabase4", requireAll = true)
        @JvmStatic
        fun setErrorViewsVisibility(
            view: View,
            apiResponse: NetworkResult<FoodJoke>?,
            database: List<FoodJokeEntity>?
        ){
            if(database != null){
                if(database.isEmpty()){
                    view.visibility = View.VISIBLE
                    if(view is TextView && apiResponse != null)
                        view.text = apiResponse.message.toString()
                }
            }
            if(apiResponse is NetworkResult.Success)
                view.visibility = View.INVISIBLE
        }
    }
}