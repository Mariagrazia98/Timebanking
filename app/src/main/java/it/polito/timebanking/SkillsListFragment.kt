package it.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import it.polito.timebanking.viewmodel.TimeSlotViewModel


class SkillsListFragment : Fragment() {
    private lateinit var timeSlotVM: TimeSlotViewModel

    private lateinit var searchView: SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String
    lateinit var title: TextView
    var adapter: MySkillRecyclerViewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_skills_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        setHasOptionsMenu(true)

        val ev: TextView = view.findViewById(R.id.empty_view)
        recyclerView = view.findViewById(R.id.rv)
        title = view.findViewById(R.id.skillsTitle)
        title.visibility = View.GONE
        val uid = arguments?.getString("userId") ?: FirebaseAuth.getInstance().currentUser?.uid
        userId = uid.toString()

        (activity as MainActivity).skills
            .observe(viewLifecycleOwner) {
                recyclerView.layoutManager = LinearLayoutManager(context)
                adapter = MySkillRecyclerViewAdapter(it.distinct(), userId)
                recyclerView.adapter = adapter

                if (it.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    title.visibility = View.GONE
                    ev.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    title.visibility = View.VISIBLE
                    ev.visibility = View.GONE
                }
            }

        (activity as MainActivity).lastSkill = ""
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_button, menu)
        val menuItem = menu.findItem(R.id.search_button)
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