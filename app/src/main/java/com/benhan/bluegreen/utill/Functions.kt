package com.benhan.bluegreen.utill

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat.getSystemService


class Functions(val context: Context) {


    fun openKeyboard(){

        val imm =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideKeyboard(editText: EditText){


        val imm =
            context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager?
        imm!!.hideSoftInputFromWindow(editText.getWindowToken(), 0)

    }



}