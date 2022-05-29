package it.polito.timebanking
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

private const val NUM_TABS = 2

class ViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return AssignedOrAcceptedTimeSlotListFragment()
            1 -> return AssignedOrAcceptedTimeSlotListFragment()
            else -> return AssignedOrAcceptedTimeSlotListFragment()
        }
    }
}