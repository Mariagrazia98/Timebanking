package it.polito.timebanking

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {
    var slotId: Long = -1
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var slot: Slot
    lateinit var dateButton: Button
    lateinit var timeButton: Button
    lateinit var titleView: EditText
    lateinit var descriptionView: EditText
    lateinit var locationView:EditText
    lateinit var durationView:EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true);
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        //delete edit_button
        menu.findItem(R.id.edit_button).isVisible = false;
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        slotId = arguments?.getLong("id")?:-1

        timeSlotVM =  ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        titleView = view.findViewById(R.id.edit_titleAdvertisement)
        locationView = view.findViewById(R.id.edit_locationAdvertisement)
        descriptionView = view.findViewById(R.id.edit_descriptionAdvertisement)
        durationView = view.findViewById(R.id.editTextNumber)

        timeSlotVM =  ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)


        //DATE
        val cal = Calendar.getInstance()
        dateButton = view.findViewById(R.id.edit_dateAdvertisement)


        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            val myFormat = "dd/MM/yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.ITALY)
            dateButton.text = sdf.format(cal.time)
        }

        dateButton.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }

        //TIME
        timeButton = view.findViewById(R.id.edit_timeAdvertisement)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            val sdf = SimpleDateFormat("HH:mm", Locale.ITALY)
            timeButton.text = sdf.format(cal.time)
        }

        timeButton.setOnClickListener {
            context?.let { it1 ->
                TimePickerDialog(
                    it1, timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true).show()
            }
        }
        if(slotId!=-1L){ //edit

            timeSlotVM.getSlotById(slotId)?.observe(viewLifecycleOwner) {
                titleView.setText(it.title)
                descriptionView.setText(it.description)
                locationView.setText(it.location)
                durationView.setText(it.duration.toString())
                dateButton.text = it.date
                timeButton.text = it.time

            }
        }
        else{ //create
            dateButton.text = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(System.currentTimeMillis())
             view.findViewById<TextView>(R.id.screenEditAdvertisement).setText("Create Advertisement")
        }



        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    slot= Slot()
                    slot.id=slotId
                    slot.date = dateButton.text.toString()
                    slot.time = timeButton.text.toString()
                    slot.title = titleView.text.toString()
                    slot.description = descriptionView.text.toString()
                    slot.duration = durationView.text.toString().toInt()
                    slot.location = locationView.text.toString()
                    if(slotId!=-1L) { //edit
                        timeSlotVM.updateSlot(slot)
                        Snackbar.make(requireView(), "Time slot updated", Snackbar.LENGTH_SHORT).show()
                    }
                    else{ //create
                        timeSlotVM.addSlot(slot)
                        Snackbar.make(requireView(), "Time slot created", Snackbar.LENGTH_SHORT).show()
                    }


                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )

    }

}