package com.example.mysimpleledger.data

import android.content.SharedPreferences
import com.example.mysimpleledger.utils.Constants
import com.example.mysimpleledger.utils.DateAndTimeHelper.getLocalDateTime
import javax.inject.Inject

class PrefManager @Inject constructor(val sharedPref: SharedPreferences, val sharedPreferencesEditor: SharedPreferences.Editor) {
    fun saveToken(token: String) {
        with(sharedPreferencesEditor) {
            this?.putString(Constants.TOKEN_KEY, token)
            this?.apply()
        }

    }

    fun saveLastSyncDateTime(){
        with(sharedPreferencesEditor){
            this.putString(Constants.DATE_TIME_KEY, getLocalDateTime())
        }
    }
    fun getLastSyncDateTime(): String {
        return sharedPref.getString(Constants.DATE_TIME_KEY, "")!!
    }

    fun saveUserEmail(email: String){
        with(sharedPreferencesEditor) {
            this?.putString(Constants.EMAIL_KEY, email)
            this?.apply()
        }
    }
    fun getEmail(): String{
        return sharedPref.getString(Constants.EMAIL_KEY, "")!!
    }

    fun clearEmail(){
        with(sharedPreferencesEditor) {
            this.putString(Constants.EMAIL_KEY, "")
            this.apply()
        }
    }

    fun getToken(): String{
        return sharedPref.getString(Constants.TOKEN_KEY, "")!!
    }

    fun clearToken() {
        with(sharedPreferencesEditor) {
            this?.putString(Constants.TOKEN_KEY, "")
            this?.apply()
        }

    }



}