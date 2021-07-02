package com.example.mysimpleledger.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}