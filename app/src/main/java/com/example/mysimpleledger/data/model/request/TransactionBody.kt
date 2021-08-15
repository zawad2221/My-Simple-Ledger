package com.example.mysimpleledger.data.model.request

import androidx.room.PrimaryKey

class TransactionBody(
        var Date: String?,
        var Amount: Float?,
        var Description: String?,
        var Contact: String?,
        var UserId: String?
) {
}