package mai.project.foody.ui.fragments.instructions

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import mai.project.foody.R
import mai.project.foody.databinding.FragmentInstructionsBinding
import mai.project.foody.models.Result
import mai.project.foody.ui.fragments.BaseFragment
import mai.project.foody.util.Constants
import mai.project.foody.util.parcelable

class InstructionsFragment : BaseFragment<FragmentInstructionsBinding>(R.layout.fragment_instructions) {

    private var argBundle: Result? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        argBundle = arguments?.parcelable(Constants.RECIPE_RESULT_KEY) as Result?
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWebView()
    }

    private fun setupWebView() {
        if (argBundle == null) return
        binding.wvInstructions.apply {
            webViewClient = object : WebViewClient() {}
            val websiteUrl: String = argBundle?.sourceUrl ?: return
            binding.wvInstructions.loadUrl(websiteUrl)
        }
    }
}