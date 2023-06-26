package mai.project.foody.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import mai.project.foody.R
import mai.project.foody.adapters.Pager2Adapter
import mai.project.foody.databinding.ActivityDetailBinding
import mai.project.foody.ui.fragments.ingredients.IngredientsFragment
import mai.project.foody.ui.fragments.instructions.InstructionsFragment
import mai.project.foody.ui.fragments.overview.OverviewFragment

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val args by navArgs<DetailActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        setActionBar()
        doInitialize()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Toolbar back button
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }

    private fun setActionBar() {
        // Set the Toolbar as the ActionBar
        binding.toolBar.apply {
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun doInitialize() {
        binding.apply {
            val fragments = ArrayList<Fragment>()
            fragments.add(OverviewFragment())
            fragments.add(IngredientsFragment())
            fragments.add(InstructionsFragment())

            val titles = ArrayList<String>()
            titles.add(getString(R.string.title_overview))
            titles.add(getString(R.string.title_ingredients))
            titles.add(getString(R.string.title_instructions))

            val resultBundle = Bundle()
            resultBundle.putParcelable("recipeBundle", args.result)

            val adapter = Pager2Adapter(
                fragments,
                resultBundle,
                this@DetailActivity.supportFragmentManager,
                lifecycle
            )

            viewPager2.isUserInputEnabled = false
            viewPager2.adapter = adapter

            TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }
}