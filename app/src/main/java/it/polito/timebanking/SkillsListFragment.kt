package it.polito.timebanking

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.viewmodel.TimeSlotViewModel


class SkillsListFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel

    val skillList = listOf("Android developer", "Electrician", "Gardening", "Bicycle repairer", "Babysitter")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_skills_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        setHasOptionsMenu(true)
        //timeSlotVM.clearSlots() //to clear the repo uncomment this and run the app

        val ev: TextView = view.findViewById(R.id.empty_view)
        val rv = view.findViewById<RecyclerView>(R.id.rv)

        timeSlotVM.getAllSkills()
            .observe(viewLifecycleOwner) {
                rv.layoutManager = LinearLayoutManager(context)
                val adapter = MySkillRecyclerViewAdapter(it)
                rv.adapter = adapter

                if (it.isEmpty()) {
                    rv.visibility = View.GONE
                    ev.visibility = View.VISIBLE
                } else {
                    rv.visibility = View.VISIBLE
                    ev.visibility = View.GONE
                }
            }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_button, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //delete edit_button
        menu.findItem(R.id.edit_button).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }
}