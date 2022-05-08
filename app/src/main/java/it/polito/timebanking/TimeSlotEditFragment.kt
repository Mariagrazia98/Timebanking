package it.polito.timebanking

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import it.polito.timebanking.model.UserFire
import it.polito.timebanking.repository.Slot
import it.polito.timebanking.viewmodel.TimeSlotViewModel
import java.text.SimpleDateFormat
import java.util.*

class TimeSlotEditFragment : Fragment(R.layout.fragment_time_slot_edit) {
    var slotId: Long = -1
    lateinit var timeSlotVM: TimeSlotViewModel
    lateinit var slot: Slot
    lateinit var dateInputLayout: TextInputLayout
    lateinit var timeInputLayout: TextInputLayout
    lateinit var titleView: EditText
    lateinit var descriptionView: EditText
    lateinit var dateView: EditText
    lateinit var timeView: EditText
    lateinit var locationView: EditText
    lateinit var durationView: EditText

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

        slotId = arguments?.getLong("id") ?: -1

        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)

        titleView = view.findViewById(R.id.edit_titleAdvertisement)
        locationView = view.findViewById(R.id.edit_locationAdvertisement)
        descriptionView = view.findViewById(R.id.edit_descriptionAdvertisement)
        durationView = view.findViewById(R.id.editTextNumber)
        dateView = view.findViewById(R.id.edit_dateAdvertisement)
        timeView = view.findViewById(R.id.edit_timeAdvertisement)

        timeSlotVM = ViewModelProvider(requireActivity()).get(TimeSlotViewModel::class.java)


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


        if (slotId != -1L) { //edit
            (activity as MainActivity).supportActionBar?.title = "Edit advertisement"
            timeSlotVM.getSlotById(slotId)?.observe(viewLifecycleOwner) {
                if (savedInstanceState == null) {
                    titleView.setText(it.title)
                    descriptionView.setText(it.description)
                    locationView.setText(it.location)
                    durationView.setText(it.duration.toString())
                    dateView.setText(it.date)
                    timeView.setText(it.time)
                } else {
                    titleView.setText(savedInstanceState.getString("title"))
                    descriptionView.setText(savedInstanceState.getString("description"))
                    locationView.setText(savedInstanceState.getString("location"))
                    durationView.setText(savedInstanceState.getString("duration"))
                    dateView.setText(savedInstanceState.getString("date"))
                    timeView.setText(savedInstanceState.getString("time"))
                }
            }
        } else { //create
            (activity as MainActivity).supportActionBar?.title = "Create advertisement"
            dateView.setText(
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.ITALY
                ).format(System.currentTimeMillis())
            )
        }

        handleButton()


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("title", titleView.text.toString())
        outState.putString("description", descriptionView.text.toString())
        outState.putString("duration", durationView.text.toString())
        outState.putString("location", locationView.text.toString())
        outState.putString("date", dateView.text.toString())
        outState.putString("time", timeView.text.toString())
    }

    private fun handleButton() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                    builder.setMessage("Do you want to update the slot?")
                        .setPositiveButton("Confirm") { dialog, id ->
                            slot = Slot()
                            slot.id = slotId
                            slot.date = dateView.text.toString()
                            slot.time = timeView.text.toString()
                            slot.title = titleView.text.toString()
                            slot.description = descriptionView.text.toString()
                            slot.duration = durationView.text.toString().toInt()
                            slot.location = locationView.text.toString()
                            if (slotId != -1L) { //edit
                                timeSlotVM.updateSlot(slot)
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    "Time slot updated!",
                                    Snackbar.LENGTH_SHORT
                                )
                                val sbView: View = snackbar.view
                                context?.let { ContextCompat.getColor(it, R.color.primary_light) }
                                    ?.let { it2 -> sbView.setBackgroundColor(it2) }

                                context?.let { it1 ->
                                    ContextCompat.getColor(
                                        it1,
                                        R.color.primary_text
                                    )
                                }
                                    ?.let { it2 -> snackbar.setTextColor(it2) }
                                snackbar.show()
                            } else { //create
                                timeSlotVM.addSlot(slot)
                                val snackbar = Snackbar.make(
                                    requireView(),
                                    "Time slot created!",
                                    Snackbar.LENGTH_SHORT
                                )
                                val sbView: View = snackbar.view
                                context?.let { ContextCompat.getColor(it, R.color.primary_light) }
                                    ?.let { it2 -> sbView.setBackgroundColor(it2) }

                                context?.let { it1 ->
                                    ContextCompat.getColor(
                                        it1,
                                        R.color.primary_text
                                    )
                                }
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
            })
    }

}