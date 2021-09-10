package com.dz.mobile.gympaiement.view.Ext

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
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
    Toast.makeText(requireContext(),text,Toast.LENGTH_LONG).show()
}

fun Date.toStringFormat(): String = SimpleDateFormat("yyyy-MM-dd").format(this)

fun String.toDateFormat(): Date = SimpleDateFormat("yyyy-MM-dd").parse(this)

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