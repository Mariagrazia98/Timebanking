package it.polito.timebanking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.polito.timebanking.model.TimeSlotFire
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.viewmodel.ProfileViewModel
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var profileVM: ProfileViewModel
    lateinit var slot: Slot
    lateinit var userId: String
    var timeslot: TimeSlotFire? = null
    var user : UserFire? = null

    lateinit var userSkills: MutableList<String>
    var timeslotSkills: MutableList<String> = mutableListOf()

    lateinit var dateInputLayout: TextInputLayout
    lateinit var timeInputLayout: TextInputLayout
    lateinit var titleView: EditText
    lateinit var descriptionView: EditText
    lateinit var dateView: EditText
    lateinit var timeView: EditText
    lateinit var locationView: EditText
    lateinit var durationView: EditText
    lateinit var skillsGroup: ChipGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //delete edit_button
        menu.findItem(R.id.edit_button).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getString("userId")?:"" //if edit userId == ""
        timeslot = arguments?.getSerializable("slot") as TimeSlotFire?
        user = arguments?.getSerializable("user") as UserFire?

        timeSlotVM =  ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)
        profileVM =  ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)

        titleView = view.findViewById(R.id.edit_titleAdvertisement)
        locationView = view.findViewById(R.id.edit_locationAdvertisement)
        descriptionView = view.findViewById(R.id.edit_descriptionAdvertisement)
        durationView = view.findViewById(R.id.editTextNumber)
        dateView = view.findViewById(R.id.edit_dateAdvertisement)
        timeView = view.findViewById(R.id.edit_timeAdvertisement)
        skillsGroup = view.findViewById(R.id.skillsAdvertisement)


        //DATE
        val cal = Calendar.getInstance()
        dateInputLayout = view.findViewById(R.id.dateInput)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.ITALY)
                dateView.setText(sdf.format(cal.time))
            }

        dateInputLayout.setEndIconOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1, R.style.DialogTheme, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        //TIME
        timeInputLayout = view.findViewById(R.id.timeInput)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat("HH:mm", Locale.ITALY)
            timeView.setText(sdf.format(cal.time))
        }

        timeInputLayout.setEndIconOnClickListener {
            context?.let { it1 ->
                TimePickerDialog(
                    it1, R.style.DialogTheme, timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true
                ).show()
            }
        }

        if(timeslot != null){ //edit
            (activity as MainActivity).supportActionBar?.title = "Edit advertisement"

            if(savedInstanceState != null){
                timeslot = timeSlotVM.getSlot()
            }
            titleView.setText(timeslot!!.title)
            descriptionView.setText(timeslot!!.description)
            locationView.setText(timeslot!!.location)
            durationView.setText(timeslot!!.duration.toString())
            dateView.setText(timeslot!!.date)
            timeView.setText(timeslot!!.time)

            userSkills = user!!.skills
            if (userSkills.isNotEmpty()) {
                userSkills.forEach { skill ->
                    addChip(userId, skill.trim())
                }
            }
        }else{ //create
            (activity as MainActivity).supportActionBar?.title = "Create advertisement"
            dateView.setText(SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(System.currentTimeMillis()))
            profileVM.getUserById(userId).observe(viewLifecycleOwner) {
                userSkills = it?.skills ?: mutableListOf()
                if (userSkills.isNotEmpty()) {
                    userSkills.forEach { skill ->
                        addChip(userId, skill.trim())
                    }
                }
            }
        }

        handleButton()
    }

    private fun handleButton() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                    val message = if(userId=="") "Do you want to update the slot?" else "Do you want to create this slot?"
                    builder.setMessage(message)
                        .setPositiveButton("Confirm") { dialog, id ->
                            val ts = TimeSlotFire()
                            ts.date = dateView.text.toString()
                            ts.time = timeView.text.toString()
                            ts.title = titleView.text.toString()
                            ts.description = descriptionView.text.toString()
                            ts.duration = durationView.text.toString().toInt()
                            ts.location = locationView.text.toString()
                            ts.skills = timeslotSkills
                            if(userId == "") { //edit
                                ts.id = timeslot!!.id
                                timeSlotVM.updateSlotF(user!!.uid,ts)
                                val snackbar = Snackbar.make(requireView(), "Time slot updated!", Snackbar.LENGTH_SHORT)
                                val sbView: View = snackbar.view
                                context?.let { ContextCompat.getColor(it, R.color.primary_light) }
                                    ?.let { it2 -> sbView.setBackgroundColor(it2) }

                                context?.let { it1 -> ContextCompat.getColor(it1, R.color.primary_text) }
                                    ?.let { it2 -> snackbar.setTextColor(it2) }
                                snackbar.show()
                            }
                            else{ //create
                                val newId = timeSlotVM.getNewSlotId(userId)
                                ts.id = newId
                                timeSlotVM.updateSlotF(userId, ts)
                                val snackbar = Snackbar.make(requireView(), "Time slot created!", Snackbar.LENGTH_SHORT)
                                val sbView: View = snackbar.view
                                context?.let { ContextCompat.getColor(it, R.color.primary_light) }
                                    ?.let { it2 -> sbView.setBackgroundColor(it2) }

                                context?.let { it1 -> ContextCompat.getColor(it1, R.color.primary_text) }
                                    ?.let { it2 -> snackbar.setTextColor(it2) }
                                snackbar.show()
                            }

                            if (isEnabled) {
                                isEnabled = false
                                requireActivity().onBackPressed()
                            }
                        }
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                            // User cancelled the dialog
                            // if you want onBackPressed() to be called as normal afterwards
                            if (isEnabled) {
                                isEnabled = false
                                requireActivity().onBackPressed()
                            }
                        })
                    builder.show()
                }
            }
        )
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeslot?.title = titleView.text.toString()
        timeslot?.description = descriptionView.text.toString()
        timeslot?.duration =  durationView.text.toString().toInt()
        timeslot?.location = locationView.text.toString()
        timeslot?.date = dateView.text.toString()
        timeslot?.time = timeView.text.toString()
        timeslot?.skills = timeslotSkills
        timeSlotVM.setSlot(timeslot!!)
    }

    private fun addChip(userId:String, text: String) {
        val chip = Chip(this.context)
        chip.text = text
        chip.isCloseIconVisible = false
        chip.isChipIconVisible = false
        chip.isCheckable = true
        if(userId == "" && timeslot!!.skills.contains(text)){
            chip.isChecked = true
            timeslotSkills.add(chip.text.toString())
        }
        chip.chipBackgroundColor =
            this.context?.let { ContextCompat.getColor(it, R.color.primary_light) }?.let {
                ColorStateList.valueOf(it)
            }
        skillsGroup.addView(chip)
        chip.setOnCheckedChangeListener { _, _ ->
            registerFilterChanged(chip)
        }
    }

    private fun registerFilterChanged(chip: Chip) {
        if(chip.isChecked)
            timeslotSkills.add(chip.text.toString())
        else
            timeslotSkills.remove(chip.text.toString())
    }
}