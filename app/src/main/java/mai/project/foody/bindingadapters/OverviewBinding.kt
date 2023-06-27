package mai.project.foody.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import mai.project.foody.R

class OverviewBinding {

    companion object {

        @BindingAdapter("updateColors")
        @JvmStatic
        fun updateColors(view: View, stateIsOn: Boolean) {
            when (view) {
                is ImageView -> {
                    if (stateIsOn)
                        view.setColorFilter(ContextCompat.getColor(view.context, R.color.green))
                }

                is TextView -> {
                    if (stateIsOn)
                        view.setTextColor(ContextCompat.getColor(view.context, R.color.green))
                }
            }
        }
    }
}