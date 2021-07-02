package com.example.mysimpleledger.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mysimpleledger.data.model.Transaction

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class TransactionDatabase: RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}