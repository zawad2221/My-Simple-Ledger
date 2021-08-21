package com.example.mysimpleledger.data.room

import androidx.room.*
import com.example.mysimpleledger.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    suspend fun getAllTransaction(): List<Transaction>

    @Insert()
    suspend fun insertTransactions(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions WHERE NULLIF(Id, '') IS NULL")
    suspend fun getNonsyncData(): List<Transaction>

    @Update()
    suspend fun updateTransactions(transactions: List<Transaction>)
}