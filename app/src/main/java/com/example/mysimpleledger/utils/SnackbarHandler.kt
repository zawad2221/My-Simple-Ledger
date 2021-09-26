package com.example.mysimpleledger.utils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.example.mysimpleledger.R
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

open class SnackbarHandler @Inject constructor() {
    fun showSnackbar(message: String, mContext: Context) {
        val rootView =
            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun showSnackbar(message: String, mView: View) {
        val snackbar = Snackbar.make(mView, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun showInternetIssue(mContext: Context) {
        val rootView =
            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(
            rootView,
            mContext.getString(R.string.internet_error),
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorRed))
        snackbar.show()
    }

    fun showInternetIssue(mView: View) {
        val snackbar = Snackbar.make(
            mView,
            mView.context.getString(R.string.internet_error),
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(mView.context, R.color.colorRed))
        snackbar.show()
    }

    fun showErrorMsg(errMsg: String, mContext: Context) {
        val rootView =
            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(
            rootView,
            errMsg,
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorRed))
        snackbar.show()
    }

    fun showErrorMsg(errMsg: String, view: View) {
        val snackbar = Snackbar.make(
            view,
            errMsg,
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(view.context, R.color.colorRed))
        snackbar.show()
    }


    fun showSuccessMsg(successMsg: String, mContext: Context) {
        val rootView =
            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(
            rootView,
            successMsg,
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen))
        snackbar.show()
    }

    fun showSuccessMsg(successMsg: String, mView: View) {

        val snackbar = Snackbar.make(
            mView,
            successMsg,
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(mView.context, R.color.colorGreen))
        snackbar.show()
    }

    fun showNeutralMsg(msg: String, mContext: Context) {
        val rootView =
            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(
            rootView,
            msg,
            Snackbar.LENGTH_LONG
        )
        snackbar.show()
    }

    fun showSuccess(mContext: Context) {
        val rootView =
            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
        val snackbar = Snackbar.make(
            rootView,
            mContext.getString(R.string.success),
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorGreen))
        snackbar.show()
    }

    fun showSuccess(mView: View) {

        val snackbar = Snackbar.make(
            mView,
            mView.context.getString(R.string.success),
            Snackbar.LENGTH_LONG
        )
        snackbar.view.setBackgroundColor(ContextCompat.getColor(mView.context, R.color.colorGreen))
        snackbar.show()
    }

//    fun showSnackbarAndLogin(message: String, mContext: Context) {
//        PrefManager.saveToSharedPref("")
//        val rootView =
//            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
//        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
//        val callback = object : Snackbar.Callback() {
//
//            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
//                super.onDismissed(transientBottomBar, event)
//                mContext.startActivity(Intent(mContext, LoginActivity::class.java))
//                mContext.finish()
//            }
//        }
//        snackbar.addCallback(callback)
//        snackbar.show()
//    }

//    fun showSnackbarAndClose(message: String, mContext: Context) {
//        val rootView =
//            (mContext as Activity).window.decorView.findViewById<View>(android.R.id.content)
//        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
//        val callback = object : Snackbar.Callback() {
//
//            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
//                super.onDismissed(transientBottomBar, event)
//                mContext.finish()
//            }
//        }
//        snackbar.addCallback(callback)
//        snackbar.show()
//    }
}