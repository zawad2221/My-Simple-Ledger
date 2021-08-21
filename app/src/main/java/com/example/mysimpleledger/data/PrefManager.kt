package com.example.mysimpleledger.data

import android.content.SharedPreferences
import com.example.mysimpleledger.utils.Constants
import javax.inject.Inject

class PrefManager {
    @Inject
    lateinit var sharedPref: SharedPreferences
    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor


    fun saveToken(token: String) {
        with(sharedPreferencesEditor) {
            this?.putString(Constants.TOKEN_KEY, token)
            this?.apply()
        }

    }

    fun clearToken() {
        with(sharedPreferencesEditor) {
            this?.putString(Constants.TOKEN_KEY, "")
            this?.apply()
        }

    }


}