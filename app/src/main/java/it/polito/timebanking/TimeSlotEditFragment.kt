package it.polito.timebanking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER



class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var slot: Slot
    lateinit var dateButton: Button
    lateinit var timeButton: Button
    lateinit var titleView: EditText
    lateinit var descriptionView: EditText
    lateinit var locationView:EditText
    lateinit var durationView:EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //findIds
        titleView = view.findViewById(R.id.edit_titleAdvertisement)
        locationView = view.findViewById(R.id.edit_locationAdvertisement)
        descriptionView = view.findViewById(R.id.edit_descriptionAdvertisement)
        durationView = view.findViewById(R.id.editTextNumber)


        timeSlotVM =  ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        val cal = Calendar.getInstance()

        //DATE
        dateButton = view.findViewById(R.id.edit_dateAdvertisement)
        dateButton.text = SimpleDateFormat("dd/MM/yyyy", Locale.ITALY).format(System.currentTimeMillis())

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

    }


    override fun onPause() {
        slot= Slot()
        slot.date = dateButton.text.toString()
        slot.time = dateButton.text.toString()
        slot.title = titleView.text.toString()
        slot.description = descriptionView.text.toString()
        slot.duration = durationView.text.toString().toInt()
        slot.location = locationView.text.toString()
        timeSlotVM.addSlot(slot)
        super.onPause()
    }
}