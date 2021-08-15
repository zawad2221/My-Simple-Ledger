package com.example.mysimpleledger.utils

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController

fun NavController.navigateUpOrFinish(activity: FragmentActivity): Boolean {
    return if (navigateUp()) {
        true
    } else {
        activity.finish()
        true
    }

}