package it.polito.timebanking.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import it.polito.timebanking.MainActivity
import it.polito.timebanking.R
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class InterestedTimeSlotListFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_slot_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        userId = arguments?.getString("userId").toString()
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val ev: TextView = view.findViewById(R.id.empty_view)
        val fab: View = view.findViewById(R.id.fab)
        (activity as MainActivity).interestedSlots.observe(viewLifecycleOwner){
                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter = InterestedTimeSlotAdapter(it, "interested", null)
                if(it.isEmpty()){
                    rv.visibility = View.GONE
                    ev.visibility = View.VISIBLE
                }else {
                    rv.visibility = View.VISIBLE
                    ev.visibility = View.GONE
                }
            }
        fab.visibility = View.GONE

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