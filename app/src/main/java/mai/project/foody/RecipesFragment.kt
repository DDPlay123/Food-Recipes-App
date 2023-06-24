package mai.project.foody

import android.os.Bundle
import android.view.View
import mai.project.foody.databinding.FragmentRecipesBinding

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