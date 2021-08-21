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
        var Status: Int,
        var Id: String? = null,
        @PrimaryKey(autoGenerate = true) var offlineId: Int? = null,
        var isBackup: Boolean? = false,
        var UserId: String? = null

)
{





}