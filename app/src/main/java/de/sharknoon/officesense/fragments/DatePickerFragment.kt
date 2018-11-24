package de.sharknoon.officesense.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import org.threeten.bp.LocalDate

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var localDateConsumer: (LocalDate) -> Unit = {}
    var currentDate = LocalDate.now()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val year = currentDate.year
        val month = currentDate.monthValue - 1
        val day = currentDate.dayOfMonth

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        currentDate = LocalDate.of(year, month + 1, day)
        localDateConsumer.invoke(currentDate)
    }
}