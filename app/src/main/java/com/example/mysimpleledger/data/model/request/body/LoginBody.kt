package com.example.mysimpleledger.data.model.request.body

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LoginBody(
        @SerializedName("UserName")
        var userName: String? = null,
        @SerializedName("Password")
        var password: String? = null
): Serializable
