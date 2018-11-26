package de.sharknoon.officesense.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.DatePicker
import org.threeten.bp.LocalDate

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var localDateConsumer: (LocalDate) -> Unit = {}
    var currentDate: LocalDate = LocalDate.now()
    var maxDate = currentDate

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val year = currentDate.year
        val month = currentDate.monthValue - 1
        val day = currentDate.dayOfMonth

        // Create a new instance of DatePickerDialog and return it
        val datePickerDialog = DatePickerDialog(context, this, year, month, day)
        datePickerDialog.datePicker.maxDate = maxDate.toEpochDay() * 24 * 60 * 60 * 1000
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        currentDate = LocalDate.of(year, month + 1, day)
        localDateConsumer.invoke(currentDate)
    }
}