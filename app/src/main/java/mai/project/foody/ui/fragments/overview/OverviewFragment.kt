package mai.project.foody.ui.fragments.overview

import android.os.Bundle
import mai.project.foody.R
import mai.project.foody.databinding.FragmentOverviewBinding
import mai.project.foody.models.Result
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.Constants
import mai.project.foody.util.parcelable

class OverviewFragment : BaseFragment<FragmentOverviewBinding>(R.layout.fragment_overview) {

    private var argBundle: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        argBundle = arguments?.parcelable(Constants.RECIPE_RESULT_KEY) as Result?
    }

    override fun FragmentOverviewBinding.initialize() {
        if (argBundle == null) return
        // Setup data
        binding.result = argBundle
    }
}