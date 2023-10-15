package hr.foi.rampu.memento

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hr.foi.rampu.memento.adapters.MainPagerAdapter
import hr.foi.rampu.memento.fragments.CompletedFragment
import hr.foi.rampu.memento.fragments.NewsFragment
import hr.foi.rampu.memento.fragments.PendingFragment

class MainActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connectViewPagerWithTabLayout()
    }

    private fun connectViewPagerWithTabLayout() {
        tabLayout = findViewById(R.id.tabs)
        viewPager2 = findViewById(R.id.viewpager)

        val mainPageAdapter = createMainPageAdapter()
        viewPager2.adapter = mainPageAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(mainPageAdapter.fragmentItems[position].titleRes)
            tab.setIcon(mainPageAdapter.fragmentItems[position].iconRes)
        }.attach()
    }

    private fun createMainPageAdapter(): MainPagerAdapter {
        val mainPagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle)
        fillAdapterWithFragments(mainPagerAdapter)
        return mainPagerAdapter
    }

    private fun fillAdapterWithFragments(adapter: MainPagerAdapter) {
        adapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.tasks_pending,
                R.drawable.baseline_assignment_late_24,
                PendingFragment::class
            )
        )
        adapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.tasks_completed,
                R.drawable.baseline_assignment_turned_in_24,
                CompletedFragment::class
            )
        )
        adapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.news,
                R.drawable.baseline_wysiwyg_24,
                NewsFragment::class
            )
        )
    }
}
