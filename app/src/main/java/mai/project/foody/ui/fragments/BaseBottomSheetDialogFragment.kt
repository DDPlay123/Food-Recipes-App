package mai.project.foody.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import mai.project.foody.R

open class BaseBottomSheetDialogFragment<VB : ViewDataBinding>(@LayoutRes val layoutRes: Int) : BottomSheetDialogFragment() {

    private var _binding: VB? = null
    val binding : VB
        get() = _binding!!

    open fun VB.initialize() {}
    open fun VB.destroy() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottomSheetDialogStyle)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.initialize()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.destroy()
        _binding = null
    }
}