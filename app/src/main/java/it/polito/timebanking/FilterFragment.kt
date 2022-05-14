package it.polito.timebanking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.polito.timebanking.model.TimeSlotFire
import java.text.SimpleDateFormat
import java.util.*

class FilterFragment : Fragment(R.layout.fragment_filter) {
    lateinit var autoCompleteTextView: AutoCompleteTextView
    lateinit var dateInputLayout: TextInputLayout
    lateinit var timeInputLayout: TextInputLayout
    lateinit var dateView: EditText
    lateinit var timeView: EditText
    lateinit var durationView : EditText
    lateinit var resetBtn : Button

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
        (activity as MainActivity).supportActionBar?.title = "Filters"
        resetBtn = view.findViewById(R.id.reset)
        resetBtn.setOnClickListener{
            (activity as MainActivity).adapterTimeSlots!!.filter.filter("reset")
            requireActivity().onBackPressed()
        }
        autoCompleteTextView = view.findViewById(R.id.orderBySpinner)
        var list = arrayListOf("Date", "Duration")
        var arrayAdapter = ArrayAdapter(
            (activity as MainActivity).applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            list
        )
        autoCompleteTextView.setAdapter(arrayAdapter)

        //DATE
        val cal = Calendar.getInstance()
        dateInputLayout = view.findViewById(R.id.dateInput)
        dateView = view.findViewById(R.id.dateEditText)
        timeView = view.findViewById(R.id.timeEditText)
        timeInputLayout = view.findViewById(R.id.timeInput)
        durationView = view.findViewById(R.id.durationEditText)
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
        callback()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun callback() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(dateView.text.isNotEmpty())
                        (activity as MainActivity).adapterTimeSlots!!.filter.filter("date="+dateView.text)
                    if(timeView.text.isNotEmpty())
                        (activity as MainActivity).adapterTimeSlots!!.filter.filter("time="+timeView.text)
                    if(durationView.text.isNotEmpty())
                        (activity as MainActivity).adapterTimeSlots!!.filter.filter("duration="+durationView.text)

                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }
}