package mai.project.foody.bindingadapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import coil.load
import mai.project.foody.R
import mai.project.foody.util.Constants
import java.util.Locale

class IngredientsRowBinding {

    companion object {

        @BindingAdapter("loadImageFromIngredientImage")
        @JvmStatic
        fun loadImageFromIngredientImage(imageView: ImageView, image: String) {
            imageView.load(Constants.BASE_IMAGE_URL + image) {
                crossfade(600)
                error(R.drawable.error_placeholder)
            }
        }

        @BindingAdapter("setIngredientName")
        @JvmStatic
        fun setIngredientName(textView: TextView, name: String) {
            textView.text = name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
            }
        }
    }
}