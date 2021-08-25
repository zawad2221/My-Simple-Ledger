package com.example.mysimpleledger.utils

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

fun NavController.navigateUpOrFinish(activity: FragmentActivity): Boolean {
    return if (navigateUp()) {
        true
    } else {
        activity.finish()
        true
    }

}

fun showErrorInTextInputLayout(textInput: TextInputLayout, message: String?){
    textInput.error = message
}

fun showSnackBar(view: View, message: String){
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}