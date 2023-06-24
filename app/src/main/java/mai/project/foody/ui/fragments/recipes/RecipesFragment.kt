package mai.project.foody.ui.fragments.recipes

import android.os.Bundle
import android.view.View
import mai.project.foody.R
import mai.project.foody.databinding.FragmentRecipesBinding
import mai.project.foody.ui.fragments.BaseFragment

class RecipesFragment : BaseFragment<FragmentRecipesBinding>(R.layout.fragment_recipes) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doInitialize()
    }

    private fun doInitialize() {
        binding.apply {
            rvShimmer.showShimmer()
        }
    }
}