package com.example.mysimpleledger.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.room.TransactionDatabase
import com.example.mysimpleledger.network.api.TransactionApi
import com.example.mysimpleledger.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
        private val transactionApi: TransactionApi,
        private val db: TransactionDatabase
        ) {
    companion object {

    }
            private val transactionDao = db.transactionDao()
    //transaction data
    private val _transactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val transactionUiState: StateFlow<UiState> = _transactionUiState

    private val _newTransactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val newTransactionUiState: StateFlow<UiState> = _newTransactionUiState

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun saveTransaction(transactions: List<Transaction>){
        _newTransactionUiState.value = UiState.Loading
        Log.d(javaClass.name, "save transaction in repository")

        transactionDao.insertTransactions(transactions)
        _newTransactionUiState.value = UiState.Success(transactions)
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllTransaction(){
        _transactionUiState.value = UiState.Loading
        val transactions = transactionDao.getAllTransaction()
        Log.d(javaClass.name, "got data ${transactions.size}")
        _transactionUiState.value = UiState.Success(transactions)
    }
}