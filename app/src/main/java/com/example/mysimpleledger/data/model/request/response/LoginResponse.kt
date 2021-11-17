package com.example.mysimpleledger.data.model.request.response

import java.io.Serializable

data class LoginResponse(
        var token: String? =null,
        var expiration: String? = null,
        var Status: String? = null
)