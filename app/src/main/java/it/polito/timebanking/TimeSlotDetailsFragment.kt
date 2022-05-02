package it.polito.timebanking

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {
    var slotId: Long = -1
    lateinit var slot: Slot
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var dateView: TextView
    lateinit var timeView: TextView
    lateinit var titleView: TextView
    lateinit var descriptionView: TextView
    lateinit var locationView: TextView
    lateinit var durationView: TextView

    /*
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.options_menu, menu)
    }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.edit_button).isVisible = false;
        inflater.inflate(R.menu.options_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        slotId = arguments?.getLong("id")!!
        timeSlotVM =  ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        dateView = view.findViewById(R.id.dateAdvertisement)
        timeView = view.findViewById(R.id.timeAdvertisement)
        titleView = view.findViewById(R.id.titleAdvertisement)
        descriptionView = view.findViewById(R.id.descriptionAdvertisement)
        locationView = view.findViewById(R.id.locationAdvertisement)
        durationView = view.findViewById(R.id.durationAdvertisement)

        timeSlotVM.getSlotById(slotId)?.observe(viewLifecycleOwner) {
            dateView.text = it.date
            timeView.text = it.time
            titleView.text = it.title
            descriptionView.text = it.description
            locationView.text = it.location
            durationView.text = it.duration.toString()
        }

    }

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item!!, requireView().findNavController()) || super.onOptionsItemSelected(item)
    }
    */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var bundle = bundleOf("id" to slotId)
        return when (item.itemId) {
            R.id.slot -> {
                findNavController().navigate(R.id.timeSlotEditFragment, bundle)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}