package it.polito.timebanking

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.timebanking.model.TimeSlotFire
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {
    lateinit var userId: String
    lateinit var slotId: String
    var read_only = false
    lateinit var timeslot: TimeSlotFire
    lateinit var slot: Slot
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var dateView: TextView
    lateinit var timeView: TextView
    lateinit var titleView: TextView
    lateinit var descriptionView: TextView
    lateinit var locationView: TextView
    lateinit var durationView: TextView
    lateinit var skillsGroup: ChipGroup



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_button).isVisible = false
        if(!read_only) {
            inflater.inflate(R.menu.options_menu, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        slotId = arguments?.getString("id")!!
        userId = arguments?.getString("userId")!!
        read_only = arguments?.getBoolean("read_only")?:false
        timeSlotVM =  ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        dateView = view.findViewById(R.id.dateAdvertisement)
        timeView = view.findViewById(R.id.timeAdvertisement)
        titleView = view.findViewById(R.id.titleAdvertisement)
        descriptionView = view.findViewById(R.id.descriptionAdvertisement)
        locationView = view.findViewById(R.id.locationAdvertisement)
        durationView = view.findViewById(R.id.durationAdvertisement)
        skillsGroup = view.findViewById(R.id.skillsAdvertisement)

        timeSlotVM.getSlotFById(userId, slotId).observe(viewLifecycleOwner) { ts->
            if(ts != null){
                dateView.text = ts.date
                timeView.text = ts.time
                titleView.text = ts.title
                descriptionView.text = ts.description
                locationView.text = ts.location
                durationView.text = ts.duration.toString()
                if (ts.skills.isNotEmpty()) {
                    ts.skills.forEach{
                        addChip(it.trim())
                    }
                }
            }
        }

        if(read_only)
            (activity as MainActivity).supportActionBar?.title = "Offer"

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val bundle = bundleOf("id" to slotId)
        return when (item.itemId) {
            R.id.slot -> {
                findNavController().navigate(R.id.timeSlotEditFragment, bundle)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addChip(text: String) {
        val chip = Chip(this.context)
        chip.text = text
        chip.isCloseIconVisible = false
        chip.chipBackgroundColor =
            this.context?.let { ContextCompat.getColor(it, R.color.primary_light) }?.let {
                ColorStateList.valueOf(it)
            }
        skillsGroup.addView(chip)
    }
}