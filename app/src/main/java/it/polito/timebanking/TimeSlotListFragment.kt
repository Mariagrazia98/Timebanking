package it.polito.timebanking

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * A fragment representing a list of Items.
 */
class TimeSlotListFragment : Fragment() {

    private fun createItems(n: Int): MutableList<ItemSlot> {
        val l = mutableListOf<ItemSlot>()
        for (i in 1..n) {
            val i = ItemSlot(i,"Lectures$i", "15/05/22", "19:30", 120)
            l.add(i)
        }
        return l
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_time_slot_list, container, false)

        val rv = view.findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = MyTimeSlotRecyclerViewAdapter(createItems(20))
        rv.adapter = adapter

        val fab: View = view.findViewById(R.id.fab)
        fab.setOnClickListener{
            NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment2)
        }


        return view
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TimeSlotListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}