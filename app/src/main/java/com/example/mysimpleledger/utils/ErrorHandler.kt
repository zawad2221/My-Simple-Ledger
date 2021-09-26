package com.example.mysimpleledger.utils

import android.content.Context
import com.example.mysimpleledger.data.PrefManager
import javax.inject.Inject

class ErrorHandler @Inject constructor(val snackbarHandler: SnackbarHandler, val prefManager: PrefManager ) {
    fun parse(errorCode: Int): String {

        return when (errorCode) {
            404 -> "The server can not find the requested page."
            401 -> {
                prefManager.clearToken()
                "Current session has ended please login again."
            }
            400 -> "The server did not understand the request."
            403 -> "Access is forbidden to the requested page."
            405 -> "The method specified in the request is not allowed."
            415 -> "The server will not accept the request, because the media type is not supported."
            500 -> "The request was not completed. The server met an unexpected condition."
            501 -> "The request was not completed. The server did not support the functionality required."
            502 -> "Bad Gateway."
            402 -> "The request was not completed. The server received an invalid response from the upstream server."
            else -> "Something went wrong"
        }
    }


    fun handleError(
        error: String,
        mContext: Context,
        isFromLogin: Boolean = false
    ) {

        when (error) {
            "401" -> {
                prefManager.clearToken()

                if (isFromLogin) {
                    snackbarHandler.showSnackbar("Wrong email or password", mContext)
                } else
                    snackbarHandler.showErrorMsg("Session expired!", mContext)
            }

            else -> {
                snackbarHandler.showSnackbar(error, mContext)

            }
        }

    }
}