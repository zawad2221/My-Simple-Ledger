package com.example.mysimpleledger.network.api

import com.example.mysimpleledger.data.model.request.body.RegistrationBody
import com.example.mysimpleledger.data.model.request.response.RegistrationResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("authenticate/register")
    suspend fun register(
            @Body registerBody: RegistrationBody
    ): Response<RegistrationResponse>


}