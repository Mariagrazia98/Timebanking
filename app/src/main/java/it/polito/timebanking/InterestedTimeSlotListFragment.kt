package it.polito.timebanking

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class InterestedTimeSlotListFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var userId: String


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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_time_slot_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        userId = arguments?.getString("userId").toString()
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val ev: TextView = view.findViewById(R.id.empty_view)
        val fab: View = view.findViewById(R.id.fab)
        timeSlotVM.getInterestedSlotsByUser(userId)
            .observe(viewLifecycleOwner){
                rv.layoutManager = LinearLayoutManager(context)
                if(!(activity as MainActivity).keepAdapter)
                    (activity as MainActivity).adapterInterestedTimeSlots = InterestedTimeSlotRecyclerViewAdapter(it)
                else
                    (activity as MainActivity).keepAdapter = false
                rv.adapter = (activity as MainActivity).adapterInterestedTimeSlots
                if(it.values.isEmpty()){
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
        inflater.inflate(R.menu.filter_button, menu)
        menu.findItem(R.id.edit_button).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }
}