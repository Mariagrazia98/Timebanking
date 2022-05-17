package it.polito.timebanking

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import it.polito.timebanking.model.TimeSlot
import it.polito.timebanking.model.User
import it.polito.timebanking.viewmodel.TimeSlotViewModel

/**
 * A fragment representing a list of Items.
 */
class TimeSlotListFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var userId: String
    var read_only: Boolean = false
    var skill: String = ""

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
        read_only = arguments?.getBoolean("read_only")?:false
        userId = arguments?.getString("userId").toString()
        skill = arguments?.getString("skill").toString()

        val ev: TextView = view.findViewById(R.id.empty_view)
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        val fab: View = view.findViewById(R.id.fab)

        if(skill != (activity as MainActivity).lastSkill) {
            if (read_only) {
                (activity as MainActivity).slotsToObserve =
                    timeSlotVM.getSlotsBySkill(userId, skill)
                (activity as MainActivity).lastSkill = skill
            } else {
                (activity as MainActivity).slotsToObserve = timeSlotVM.getSlotsByUser(userId)
                (activity as MainActivity).lastSkill = skill
            }
        }

        if(read_only){
            (activity as MainActivity).supportActionBar?.title = "Offers list"
            fab.visibility = View.GONE
        }

        (activity as MainActivity).slotsToObserve?.observe(viewLifecycleOwner){
            rv.layoutManager = LinearLayoutManager(context)
            if(!(activity as MainActivity).keepAdapter)
                (activity as MainActivity).adapterTimeSlots = MyTimeSlotRecyclerViewAdapter(it, read_only)
            else
                (activity as MainActivity).keepAdapter = false
            rv.adapter = (activity as MainActivity).adapterTimeSlots


            if(it.isEmpty()){
                rv.visibility = View.GONE
                ev.visibility = View.VISIBLE
            }else {
                rv.visibility = View.VISIBLE
                ev.visibility = View.GONE
            }
        }

        fab.setOnClickListener{
            val bundle = bundleOf("userId" to (userId))
            NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2, bundle)
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