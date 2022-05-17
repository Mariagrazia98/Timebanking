package it.polito.timebanking

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.slider.RangeSlider
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FilterFragment : Fragment(R.layout.fragment_filter) {
    lateinit var autoCompleteTextView: AutoCompleteTextView
    lateinit var dateInputLayout: TextInputLayout
    lateinit var timeInputLayout: TextInputLayout
    lateinit var dateView: EditText
    lateinit var timeView: EditText
    lateinit var rangeSlider: RangeSlider
    lateinit var resetBtn: Button
    lateinit var applyBtn: Button
    lateinit var display: TextView
    lateinit var list : ArrayList<String>
    var mPosition : Int? = null
    var sx = 30
    var dx = 240

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
        dateInputLayout = view.findViewById(R.id.dateInput)
        dateView = view.findViewById(R.id.dateEditText)
        timeView = view.findViewById(R.id.timeEditText)
        timeInputLayout = view.findViewById(R.id.timeInput)
        applyBtn = view.findViewById(R.id.apply)
        display = view.findViewById(R.id.rangeTextView)
        rangeSlider = view.findViewById(R.id.rangeSlider)
        autoCompleteTextView = view.findViewById(R.id.orderBySpinner)
        restoreValuesFromBundle()

        resetBtn.setOnClickListener {
            (activity as MainActivity).adapterTimeSlots?.filter?.filter("reset")
            (activity as MainActivity).filterBundle = null
            requireActivity().onBackPressed()
        }

        applyBtn.setOnClickListener {
            (activity as MainActivity).adapterTimeSlots?.filter?.filter("duration= $sx - $dx")
            Thread.sleep(50)
            if (dateView.text.isNotEmpty())
                (activity as MainActivity).adapterTimeSlots!!.filter.filter("date=" + dateView.text)
            Thread.sleep(50)
            if (timeView.text.isNotEmpty())
                (activity as MainActivity).adapterTimeSlots!!.filter.filter("time=" + timeView.text)
            Thread.sleep(50)
            when (mPosition) {
                0 -> (activity as MainActivity).adapterTimeSlots!!.filter.filter("order=${list[0]}")
                1 -> (activity as MainActivity).adapterTimeSlots!!.filter.filter("order=${list[1]}")
                2 -> (activity as MainActivity).adapterTimeSlots!!.filter.filter("order=${list[2]}")
                3 -> (activity as MainActivity).adapterTimeSlots!!.filter.filter("order=${list[3]}")
            }
            (activity as MainActivity).keepAdapter = true
            saveBundle()
            requireActivity().onBackPressed()
        }

        rangeSlider.addOnChangeListener { rangeSlider, value, fromUser ->
            val values = rangeSlider.values
            sx = values[0].toInt()
            dx = values[1].toInt()
            display.text = "$sx - $dx mins"
        }

        list = arrayListOf("Date (ascending)","Date (descending)", "Duration (ascending)", "Duration (descending)")
        var arrayAdapter = ArrayAdapter(
            (activity as MainActivity).applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            list
        )
        autoCompleteTextView.setAdapter(arrayAdapter)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            // You can get the label or item that the user clicked:
            mPosition = position
        }
        //DATE
        val cal = Calendar.getInstance()
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
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createBundle() : Bundle {
        lateinit var outState : Bundle
        outState.putInt("SX_SLIDER", sx)
        outState.putInt("DX_SLIDER", dx)
        outState.putString("DATE", dateView.text.toString())
        outState.putString("TIME", timeView.text.toString())
        outState.putString("ORDER_BY", autoCompleteTextView.text.toString())
        return outState
    }

    private fun restoreValuesFromBundle(){
        val inState = (activity as MainActivity).filterBundle
        if(inState != null){
            sx = inState.getInt("SX_SLIDER")
            dx = inState.getInt("DX_SLIDER")
            rangeSlider.setValues(sx.toFloat(),dx.toFloat())
            display.text = "$sx - $dx mins"
            val strDate = inState.getString("DATE")
            val strTime = inState.getString("TIME")
            val strOrder = inState.getString("ORDER_BY")
            if(strDate?.isNotEmpty() == true)
                dateView.setText(strDate)
            if(strTime?.isNotEmpty() == true)
                timeView.setText(strTime)
            if(strOrder?.isNotEmpty() == true)
                autoCompleteTextView.setText(strOrder)
        }
        (activity as MainActivity).filterBundle = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        saveBundle()
        super.onSaveInstanceState(outState)
    }

    private fun saveBundle() {
        var outState = Bundle()
        outState.putInt("SX_SLIDER", sx)
        outState.putInt("DX_SLIDER", dx)
        outState.putString("DATE", dateView.text.toString())
        outState.putString("TIME", timeView.text.toString())
        outState.putString("ORDER_BY", autoCompleteTextView.text.toString())
        (activity as MainActivity).filterBundle = outState
    }
}