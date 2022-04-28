package it.polito.timebanking

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import it.polito.timebanking.viewmodel.TimeSlotViewModel

/**
 * A fragment representing a list of Items.
 */
class TimeSlotListFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //delete edit_button
        menu.findItem(R.id.edit_button).isVisible = false;
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_slot_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        //timeSlotVM.clearSlots() //to clear the repo uncomment this and run the app

        val ev: TextView = view.findViewById(R.id.empty_view)
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        timeSlotVM.slots?.observe(viewLifecycleOwner) {
            rv.layoutManager = LinearLayoutManager(context)
            val adapter = MyTimeSlotRecyclerViewAdapter(it)
            rv.adapter = adapter

            if(it.size<1){
                rv.visibility = View.GONE
                ev.visibility = View.VISIBLE
            }else {
                rv.visibility = View.VISIBLE;
                ev.visibility = View.GONE;
            }
        }

        val fab: View = view.findViewById(R.id.fab)
        fab.setOnClickListener{
            NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2)
        }

        return view
    }

}