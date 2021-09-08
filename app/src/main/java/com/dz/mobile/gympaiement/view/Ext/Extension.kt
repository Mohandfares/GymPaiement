package com.dz.mobile.gympaiement.view.Ext

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

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