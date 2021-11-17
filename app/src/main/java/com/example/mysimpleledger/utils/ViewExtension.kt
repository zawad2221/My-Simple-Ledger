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
fun View.makeVisible() {
    visibility = View.VISIBLE
}
fun View.enable(shouldEnable: Boolean) {
    this.isEnabled = shouldEnable
    this.alpha = if (shouldEnable) 1.0f else 0.5f
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun setVisibilityOfView(views: List<View>, visibility: Int){
    for(view in views){
        view.visibility = visibility
    }
}

fun showErrorInTextInput(layout: TextInputLayout, message: String){
    layout.error=message
    if(message==null) layout.isErrorEnabled = false
}