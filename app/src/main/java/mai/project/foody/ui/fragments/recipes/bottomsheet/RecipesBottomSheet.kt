package mai.project.foody.ui.fragments.recipes.bottomsheet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import mai.project.foody.R
import mai.project.foody.databinding.RecipesBottomSheetBinding
import mai.project.foody.ui.fragments.BaseBottomSheetDialogFragment
import mai.project.foody.util.Constants
import mai.project.foody.util.Method
import mai.project.foody.viewmodels.RecipesViewModel
import java.util.Locale

class RecipesBottomSheet : BaseBottomSheetDialogFragment<RecipesBottomSheetBinding>(R.layout.recipes_bottom_sheet) {

    private lateinit var recipesViewModel: RecipesViewModel

    private var mealTypeChip = Constants.DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
    private var dietTypeChip = Constants.DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doObserve()
        setListener()
    }

    private fun doObserve() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner) { value ->
                        mealTypeChip = value.selectedMealType
                        dietTypeChip = value.selectedDietType

                        updateChip(value.selectedMealTypeId, binding.chipGroupMealType)
                        updateChip(value.selectedDietTypeId, binding.chipGroupDietType)
                    }
                }
            }
        }
    }

    private fun setListener() {
        binding.apply {
            chipGroupMealType.setOnCheckedStateChangeListener { group, _ ->
                val chip = group.findViewById<Chip>(group.checkedChipId)
                val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
                mealTypeChip = selectedMealType
                mealTypeChipId = group.checkedChipId
            }

            chipGroupDietType.setOnCheckedStateChangeListener { group, _ ->
                val chip = group.findViewById<Chip>(group.checkedChipId)
                val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
                dietTypeChip = selectedMealType
                dietTypeChipId = group.checkedChipId
            }

            btnApply.setOnClickListener {
                recipesViewModel.saveMealAndDietType(
                    mealTypeChip,
                    mealTypeChipId,
                    dietTypeChip,
                    dietTypeChipId
                ).run { dismiss() }
            }
        }
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0) {
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true
            } catch (e: Exception) {
                e.printStackTrace()
                Method.logE("RecipesBottomSheet", "updateChip", e)
            }
        }
    }
}