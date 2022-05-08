package it.polito.timebanking

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class SkillsListFragment : Fragment() {
    lateinit var timeSlotVM: TimeSlotViewModel

    val skillList = listOf("Android developer", "Electrician", "Gardening", "Bicycle repairer", "Babysitter")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_skills_list, container, false)
        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        //timeSlotVM.clearSlots() //to clear the repo uncomment this and run the app

        val ev: TextView = view.findViewById(R.id.empty_view)
        val rv = view.findViewById<RecyclerView>(R.id.rv)

        rv.layoutManager = LinearLayoutManager(context)
        val adapter = MySkillRecyclerViewAdapter(skillList)
        rv.adapter = adapter

        if(skillList.isEmpty()){
            rv.visibility = View.GONE
            ev.visibility = View.VISIBLE
        }else {
            rv.visibility = View.VISIBLE
            ev.visibility = View.GONE
        }

        return view
    }
}