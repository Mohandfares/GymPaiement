package com.dz.mobile.gympaiement.view.Ext

import android.annotation.SuppressLint
import android.app.DatePickerDialog.OnDateSetListener
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.google.android.material.textfield.TextInputEditText
import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

inline fun EditText.onTextChanged(crossinline listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            s?.let { listener(it.toString()) }
        }
    })
}

fun Fragment.makeToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
}

fun Date.toStringFormat(): String = SimpleDateFormat("yyyy-MM-dd").format(this)

fun String.toDateFormat(): Date = SimpleDateFormat("yyyy-MM-dd").parse(this)

fun Date.monthFormat(): String = SimpleDateFormat("MM").format(this)

fun Date.month(): Int = SimpleDateFormat("MM").format(this).toInt()

fun Date.day(): Int = SimpleDateFormat("dd").format(this).toInt()

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun TextInputEditText.value() = text.toString()
fun AutoCompleteTextView.value() = text.toString()

fun Double.toStringFormat(): String {
    val format = when {
        this >= 10000 -> DecimalFormat("#0,000.00")
        this >= 100 -> DecimalFormat("#000.00")
        this >= 10 -> DecimalFormat("#00.00")
        else -> DecimalFormat("0.00")
    }
    return format.format(this).replace(",", ".")
}

@SuppressLint("RestrictedApi", "SimpleDateFormat")
inline fun Fragment.dateDialog(crossinline body: (String) -> Unit) {
    val newCalendar = Calendar.getInstance()
    val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
    val datePickerListener =
        OnDateSetListener { view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            val newDate = Calendar.getInstance()
            newDate[Calendar.YEAR] = selectedYear
            newDate[Calendar.MONTH] = selectedMonth
            newDate[Calendar.DAY_OF_MONTH] = selectedDay
            body(dateFormat.format(newDate.time))
        }
    return MaterialStyledDatePickerDialog(
        requireContext(),
        datePickerListener,
        newCalendar[Calendar.YEAR],
        newCalendar[Calendar.MONTH],
        newCalendar[Calendar.DAY_OF_MONTH]
    ).show()
}