package com.example.mysimpleledger.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.room.TransactionDatabase
import com.example.mysimpleledger.network.api.TransactionApi
import com.example.mysimpleledger.ui.TestUiState
import com.example.mysimpleledger.ui.UiState
import com.example.mysimpleledger.utils.Event
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class TransactionRepository @Inject constructor(
        private val transactionApi: TransactionApi,
        private val db: TransactionDatabase
        ) {
    var job: CompletableJob? = null
    companion object {

    }
            private val transactionDao = db.transactionDao()
    //transaction data
    private val _transactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val transactionUiState: StateFlow<UiState> = _transactionUiState

    private val _newTransactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val newTransactionUiState: StateFlow<UiState> = _newTransactionUiState

    //test
    private val _transactionUiStateTest = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionUiStateTest: StateFlow<TestUiState> = _transactionUiStateTest

    //update
    private val _transactionUpdateUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionUpdateUiState: StateFlow<TestUiState> = _transactionUpdateUiState

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun saveTransaction(transactions: List<Transaction>){
        try {
            _newTransactionUiState.value = UiState.Loading
            Log.d(javaClass.name, "save transaction in repository")

            transactionDao.insertTransactions(transactions)
            _newTransactionUiState.value = UiState.Success(transactions)
        }
        catch (e: Exception){
            _newTransactionUiState.value = UiState.Error("failed to add")
        }

    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllTransaction(){

        try {
            _transactionUiState.value = UiState.Loading
            val transactions = transactionDao.getAllTransaction()
            Log.d(javaClass.name, "got data ${transactions.size}")
            if(transactions.isNotEmpty())
                _transactionUiState.value = UiState.Success(transactions)
            else
                _transactionUiState.value = UiState.Empty
        }
        catch (e: Exception){
            _transactionUiState.value = UiState.Error("failed to load data")
        }

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllTransactionTest(){

        try {
            _transactionUiStateTest.value = TestUiState.Loading
            val transactions = transactionDao.getNonsyncData()
            Log.d(javaClass.name, "got data ${transactions.size}")
            if(transactions.isNotEmpty())
                _transactionUiStateTest.value = TestUiState.Success(Event(transactions as List<Transaction>))
            else
                _transactionUiStateTest.value = TestUiState.Empty
        }
        catch (e: Exception){
            _transactionUiStateTest.value = TestUiState.Error(Event("failed to load data"))
        }

    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateTransactions(transactions: List<Transaction>){

        try {
            _transactionUpdateUiState.value = TestUiState.Loading
            transactionDao.updateTransactions(transactions = transactions)
            Log.d(javaClass.name, "got data ${transactions.size}")
            if(transactions.isNotEmpty())
                _transactionUpdateUiState.value = TestUiState.Success(Event(transactions as List<Transaction>))
            else
                _transactionUpdateUiState.value = TestUiState.Empty
        }
        catch (e: Exception){
            _transactionUpdateUiState.value = TestUiState.Error(Event("failed to load data"))
        }

    }
}