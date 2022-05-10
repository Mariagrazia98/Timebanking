package it.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Adapter
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.io.Console


class SkillsListFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var searchView: SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String
    var adapter: MySkillRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_skills_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        setHasOptionsMenu(true)
        //timeSlotVM.clearSlots() //to clear the repo uncomment this and run the app

        val ev: TextView = view.findViewById(R.id.empty_view)
        recyclerView = view.findViewById(R.id.rv)

        userId = arguments?.getString("userId").toString()

        timeSlotVM.getAllSkills()
            .observe(viewLifecycleOwner) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                adapter = MySkillRecyclerViewAdapter(it, userId)
                recyclerView.adapter = adapter

                if (it.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    ev.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    ev.visibility = View.GONE
                }
            }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_button, menu)
        var menuItem = menu.findItem(R.id.search_button)
        searchView = menuItem.actionView as SearchView
        searchView.queryHint = "Type here to search"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(text: String): Boolean {
                adapter?.filter?.filter(text)
                return true
            }
            override fun onQueryTextSubmit(text: String): Boolean {
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //delete edit_button
        menu.findItem(R.id.edit_button).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}