package com.example.mysimpleledger.utils

fun convertDateToServerFormat(date: String): String{

    val newDate = date.split("-").toTypedArray()
    return newDate[2]+"-"+newDate[1]+"-"+newDate[0]
}
fun convertDateToUIFormat(date: String): String{

    val newDate = date.split("-").toTypedArray()
    return newDate[2]+"-"+newDate[1]+"-"+newDate[0]
}