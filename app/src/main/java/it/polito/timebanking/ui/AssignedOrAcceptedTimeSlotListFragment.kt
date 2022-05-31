package it.polito.timebanking.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.MainActivity
import it.polito.timebanking.R
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class AssignedOrAcceptedTimeSlotListFragment: Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var ownerUser: User
    lateinit var status: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_slot_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        ownerUser = arguments?.getSerializable("ownerUser") as User
        status = arguments?.getString("status").toString()

        val ev: TextView = view.findViewById(R.id.empty_view)
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab: View = view.findViewById(R.id.fab)

        if (status == "accepted") {
            (activity as MainActivity).slotsToObserve = timeSlotVM.getAcceptedSlotsByUser(ownerUser.uid)
        } else{
            (activity as MainActivity).slotsToObserve = timeSlotVM.getAssignedSlotsByUser(ownerUser.uid)
        }

        fab.visibility = View.GONE

        (activity as MainActivity).slotsToObserve?.observe(viewLifecycleOwner){
            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = InterestedTimeSlotAdapter(it, status, ownerUser)

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
        menu.findItem(R.id.edit_button).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }


}