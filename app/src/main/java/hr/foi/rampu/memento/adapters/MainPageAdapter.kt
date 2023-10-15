package hr.foi.rampu.memento.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import hr.foi.rampu.memento.R
import hr.foi.rampu.memento.fragments.CompletedFragment
import hr.foi.rampu.memento.fragments.NewsFragment
import hr.foi.rampu.memento.fragments.PendingFragment

class MainPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    val titleList = listOf(R.string.pending_fragment, R.string.completed_fragment, R.string.news_fragment)
    val iconList = listOf(
        R.drawable.baseline_assignment_late_24,
        R.drawable.baseline_assignment_turned_in_24,
        R.drawable.baseline_wysiwyg_24
    )

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingFragment()
            1 -> CompletedFragment()
            else -> NewsFragment()
        }
    }
}
