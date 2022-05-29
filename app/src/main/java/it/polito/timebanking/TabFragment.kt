package it.polito.timebanking

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment: Fragment(R.layout.fragment_tab) {
    private val tabTitle = arrayOf("Assigned", "Accepted")
    lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab, container, false)
        val pager = view.findViewById<ViewPager2>(R.id.viewPager)
        val tl = view.findViewById<TabLayout>(R.id.tabs)
        userId = arguments?.getString("userId").toString()

        pager.adapter = ViewPagerAdapter(this, userId)
        TabLayoutMediator(tl, pager){
                tab, position ->
            tab.text = tabTitle[position]
        }.attach()

        return view
    }

}