package com.example.mysimpleledger.utils

import java.text.SimpleDateFormat
import java.util.*

object DateAndTimeHelper {

    fun getLocalDateTime(): String{
        return SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.getDefault())
            .format(Calendar.getInstance().time).toString()
    }
}