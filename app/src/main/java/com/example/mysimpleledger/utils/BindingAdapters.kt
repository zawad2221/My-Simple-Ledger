package com.example.mysimpleledger.utils

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:setAmountInView")
fun setAmountInView(textView: TextView, amount: Float){
    textView.text = amount.toString()
}