package com.example.mysimpleledger.data.model

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(


        var Date: String,
        var Amount: Float,
        var Description: String,
        var Contact: String,
        var Type: Int,
        var Status: Int

)
{
    @PrimaryKey(autoGenerate = true) var offlineId: Int? = null
    var Id: String? = null
    var isBackup: Boolean? = null
    var UserId: String? = null
//
//    companion object{
//        @BindingAdapter("android:setAmountInView")
//        @JvmStatic
//        fun setAmountInView(textView: TextView, amount: Float){
//            textView.text = amount.toString()
//        }
//    }

}