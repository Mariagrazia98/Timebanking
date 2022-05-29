package it.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator

class TabFragment: Fragment(R.layout.fragment_tab) {
    private val tabTitle = arrayOf("Assigned", "Accepted")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab, container, false)
        val pager = view.findViewById<ViewPager2>(R.id.viewPager)
        val tl = view.findViewById<TabLayout>(R.id.tabs)
        pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tl, pager){
                tab, position ->
            tab.text = tabTitle[position]
        }.attach()

        tl.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                println(tab)
                pager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                return
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                return
            }
        })
        return view
    }

}