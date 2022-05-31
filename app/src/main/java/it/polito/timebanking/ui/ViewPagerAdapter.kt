package it.polito.timebanking.ui
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import it.polito.timebanking.model.User
import it.polito.timebanking.ui.AssignedOrAcceptedTimeSlotListFragment

private const val NUM_TABS = 2

class ViewPagerAdapter(fragment: Fragment, val user: User) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        val bundle = bundleOf()
        bundle.putSerializable("ownerUser", user)
        when (position) {
            0 -> {
                bundle.putString("status", "assigned")

            }
            1 -> {
                bundle.putString("status", "accepted")
            }
        }
        val assignedOrAcceptedTimeSlotListFragment = AssignedOrAcceptedTimeSlotListFragment()
        assignedOrAcceptedTimeSlotListFragment.arguments = bundle
        return assignedOrAcceptedTimeSlotListFragment
    }
}