package it.polito.timebanking.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import it.polito.timebanking.R
import it.polito.timebanking.ui.ViewPagerAdapter
import it.polito.timebanking.viewmodel.ProfileViewModel

class TabFragment: Fragment(R.layout.fragment_tab) {
    private val tabTitle = arrayOf("Assigned to me", "Accepted by me")
    lateinit var userId: String
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab, container, false)
        val pager = view.findViewById<ViewPager2>(R.id.viewPager)
        val tl = view.findViewById<TabLayout>(R.id.tabs)
        userId = arguments?.getString("userId").toString()

        profileViewModel.getUserById(userId)
            .observe(viewLifecycleOwner){
                pager.adapter = it?.let { it1 -> ViewPagerAdapter(this, it1) }

                TabLayoutMediator(tl, pager){
                        tab, position ->
                    tab.text = tabTitle[position]
                }.attach()
        }
        return view
    }

}