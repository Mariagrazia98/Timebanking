package it.polito.timebanking

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import it.polito.timebanking.model.TimeSlotFire
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.viewmodel.TimeSlotViewModel

class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_details) {
   // lateinit var slotId: String
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
    lateinit var profileButton: LinearLayout
    lateinit var profileNameView: TextView
    lateinit var profileImageView: ImageView
    lateinit var user : UserFire



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
        //slotId = arguments?.getString("id")!!
        user = (arguments?.getSerializable("user") as UserFire?)!!
        timeslot = (arguments?.getSerializable("slot") as TimeSlotFire?)!!
        read_only = arguments?.getBoolean("read_only")?:false
        timeSlotVM =  ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        dateView = view.findViewById(R.id.dateAdvertisement)
        timeView = view.findViewById(R.id.timeAdvertisement)
        titleView = view.findViewById(R.id.titleAdvertisement)
        descriptionView = view.findViewById(R.id.descriptionAdvertisement)
        locationView = view.findViewById(R.id.locationAdvertisement)
        durationView = view.findViewById(R.id.durationAdvertisement)
        skillsGroup = view.findViewById(R.id.skillsAdvertisement)
        profileButton = view.findViewById(R.id.profileLink)
        profileNameView = view.findViewById(R.id.offererName)
        profileImageView = view.findViewById(R.id.imageViewSlot)


        timeSlotVM.getSlotFById(user.uid, timeslot.id).observe(viewLifecycleOwner) { t ->
            if (t != null) {
                dateView.text = t.date
                timeView.text = t.time
                titleView.text = t.title
                descriptionView.text = t.description
                locationView.text = t.location
                durationView.text = t.duration.toString()
                if (t.skills.isNotEmpty()) {
                    t.skills.forEach {
                        addChip(it.trim())
                    }
                }
            }
        }

        if(read_only) {
            (activity as MainActivity).supportActionBar?.title = "Offer"

            profileNameView.text = user.fullname
            // Download directly from StorageReference using Glide
            if(user.imagePath!=null)
                Glide.with(this /* context */).load(user.imagePath).into(profileImageView)

            profileButton.setOnClickListener{
                val bundle = bundleOf("userId" to (user.uid), "read_only" to read_only)
                NavHostFragment.findNavController(FragmentManager.findFragment(it)).navigate(R.id.action_timeSlotDetailsFragment_to_showProfileFragment, bundle)
            }
        }else{
            profileButton.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //to edit timeslot
        val bundle = bundleOf()
        bundle.putSerializable("slot", timeslot)
        bundle.putSerializable("user", user)
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