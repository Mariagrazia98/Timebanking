package it.polito.timebanking

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class AssignedOrAcceptedTimeSlotListFragment: Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var userId: String
    lateinit var status: String

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.filterBtn -> {
                findNavController().navigate(R.id.action_timeSlotListFragment_to_filterFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_slot_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        userId = arguments?.getString("userId").toString()
        status = arguments?.getString("status").toString()

        val ev: TextView = view.findViewById(R.id.empty_view)
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab: View = view.findViewById(R.id.fab)

        if (status == "accepted") {
            (activity as MainActivity).slotsToObserve = timeSlotVM.getAcceptedSlotsByUser(userId)
        } else{
            (activity as MainActivity).slotsToObserve = timeSlotVM.getAssignedSlotsByUser(userId)
        }

        fab.visibility = View.GONE

        (activity as MainActivity).slotsToObserve?.observe(viewLifecycleOwner){
            rv.layoutManager = LinearLayoutManager(context)

            if(!(activity as MainActivity).keepAdapter)
                (activity as MainActivity).adapterAssignedOrAcceptedTimeSlots = AssignedOrAcceptedTimeSlotRecyclerViewAdapter(it)
            else
                (activity as MainActivity).keepAdapter = false
            rv.adapter = (activity as MainActivity).adapterAssignedOrAcceptedTimeSlots

            if(it.values.isEmpty()){
                rv.visibility = View.GONE
                ev.visibility = View.VISIBLE
            }else {
                rv.visibility = View.VISIBLE
                ev.visibility = View.GONE
            }
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.filter_button, menu)
        menu.findItem(R.id.edit_button).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }


}