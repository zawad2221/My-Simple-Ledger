package com.example.mysimpleledger.network.api

import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.model.request.TransactionBody
import retrofit2.Response
import retrofit2.http.*

interface TransactionApi {
    @POST("authenticate/register")
    fun register()

    @GET("transaction/all/{UserId}")
    suspend fun getTransactionByUserId(
            @Path(value = "UserId", encoded = true) userId: String
    ):Response<List<Transaction>>

    @POST("transaction/add")
    suspend fun addTransaction(@Header("Content-Type") header: String, @Body transaction: Transaction): Response<Transaction>
}