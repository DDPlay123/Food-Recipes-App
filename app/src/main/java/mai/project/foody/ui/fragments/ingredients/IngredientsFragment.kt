package mai.project.foody.ui.fragments.ingredients

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import mai.project.foody.R
import mai.project.foody.adapters.IngredientsAdapter
import mai.project.foody.databinding.FragmentIngredientsBinding
import mai.project.foody.models.Result
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.Constants
import mai.project.foody.util.parcelable

class IngredientsFragment : BaseFragment<FragmentIngredientsBinding>(R.layout.fragment_ingredients) {

    private var argBundle: Result? = null
    private val ingredientsAdapter by lazy { IngredientsAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        argBundle = arguments?.parcelable(Constants.RECIPE_RESULT_KEY) as Result?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvIngredients.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(requireContext())
            argBundle?.let { ingredientsAdapter.setData(it.extendedIngredients) }
        }
    }
}