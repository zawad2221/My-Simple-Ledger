package com.example.mysimpleledger.data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.model.request.TransactionBody
import com.example.mysimpleledger.data.room.TransactionDatabase
import com.example.mysimpleledger.network.api.TransactionApi
import com.example.mysimpleledger.ui.TestUiState
import com.example.mysimpleledger.ui.UiState
import com.example.mysimpleledger.utils.Event
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
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
    private val _transactionUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionUiState: StateFlow<TestUiState> = _transactionUiState

    private val _newTransactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val newTransactionUiState: StateFlow<UiState> = _newTransactionUiState

    //test
    private val _transactionUiStateTest = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionUiStateTest: StateFlow<TestUiState> = _transactionUiStateTest

    //update
    private val _transactionUpdateUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionUpdateUiState: StateFlow<TestUiState> = _transactionUpdateUiState

    //new offline transaction
    private val _newAllTransactionUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val newAllTransactionUiState: StateFlow<TestUiState> = _newAllTransactionUiState


    var jobOffline: Job? = null
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
        jobOffline = CoroutineScope(IO).launch {
            try {
                _transactionUiState.value = TestUiState.Loading
                val transactions = transactionDao.getAllTransaction()
                Log.d(javaClass.name, "got data ${transactions.size}")
//            if(transactions.isNotEmpty())
                _transactionUiState.value = TestUiState.Success(Event(transactions as List<Transaction>))
//            else
//                _transactionUiState.value = TestUiState.Empty
                jobOffline?.cancel()
            }
            catch (e: Exception){
                _transactionUiState.value = TestUiState.Error(Event("failed to load data"))
            }
        }



    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllNewTransaction(){
        jobOffline = Job()
        jobOffline = CoroutineScope(IO).launch {
            try {
                kotlin.runCatching {
                    withContext(Main){
                        _newAllTransactionUiState.value = TestUiState.Loading
                    }
                    transactionDao.getNonsyncData()
                }.onSuccess {
                    withContext(Main){
                        _newAllTransactionUiState.value = TestUiState.Success(Event(it as List<Transaction>))
                        //jobOffline?.cancel()
                        //else _newAllTransactionUiState.value = TestUiState.Empty
                    }
                }.onFailure {
                    _newAllTransactionUiState.value = TestUiState.Error(Event("failed to load data"))
                    Log.d(javaClass.name, "failed to load data 1 ${it.message}")
                    //jobOffline?.cancel()
                }
            }
            catch (e: Exception){
                _newAllTransactionUiState.value = TestUiState.Error(Event("failed to load data"))
                Log.d(javaClass.name, "failed to load data 2 ${e.message}")
                //jobOffline?.cancel()
            }
        }



    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllTransactionTest(){
        jobOffline = Job()
        jobOffline = CoroutineScope(IO).launch {
            try {
                kotlin.runCatching {
                    withContext(Main){
                        _transactionUiStateTest.value = TestUiState.Loading
                    }
                    transactionDao.getAllTransaction()
                }.onSuccess {
                    withContext(Main){
                        _transactionUiStateTest.value = TestUiState.Success(Event(it as List<Transaction>))
                        //jobOffline?.cancel()
                    }
                }.onFailure {
                    _transactionUiStateTest.value = TestUiState.Error(Event("failed to load data"))
                    Log.d(javaClass.name, "failed to load data 1 ${it.message}")
                    //jobOffline?.cancel()
                }
            }
            catch (e: Exception){
                _transactionUiStateTest.value = TestUiState.Error(Event("failed to load data"))
                Log.d(javaClass.name, "failed to load data 2 ${e.message}")
                //jobOffline?.cancel()
            }
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

    //get data from server
    private val _transactionServerUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionServerUiState: StateFlow<TestUiState> = _transactionServerUiState
    var serverJob: Job? = null
    fun getTransactionByUserId(userId: String){
        serverJob = Job()

            serverJob = CoroutineScope(IO).launch{
                kotlin.runCatching {
                    withContext(Main){
                        _transactionServerUiState.value = TestUiState.Loading
                    }
                    transactionApi.getTransactionByUserId(userId)
                }.onSuccess {
                    if(it.isSuccessful && it.body()!=null){
                        withContext(Main){
                            _transactionServerUiState.value = TestUiState.Success(Event(it.body() as List<Transaction>))
                            cancelJob()
                        }
                    }
                    else{
                        withContext(Main){
                            _transactionServerUiState.value = TestUiState.Error(Event(it.message()))
                            Log.d(javaClass.name, "onFailure0 :${it.message()}")
                            cancelJob()
                        }
                    }

                }.onFailure {
                    withContext(Main){
                        _transactionServerUiState.value = TestUiState.Error(Event(it.toString()))
                        Log.d(javaClass.name, "onFailure: ${it.message}")
                        cancelJob()
                    }
                }

            }

    }
    fun cancelJob(){
        serverJob?.cancel()
    }

    //save data in server
    private val _newTransactionServerUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val newTransactionServerUiState: StateFlow<TestUiState> = _newTransactionServerUiState
    var newServerJob: Job? = null
    fun saveDataInServer(transaction: Transaction){
        newServerJob = Job()

        newServerJob = CoroutineScope(IO).launch{
            kotlin.runCatching {
                withContext(Main){
                    _newTransactionServerUiState.value = TestUiState.Loading
                }
                transactionApi.addTransaction("application/json",transaction)
            }.onSuccess {
                if(it.isSuccessful && it.body()!=null){
                    withContext(Main){
                        _newTransactionServerUiState.value = TestUiState.Success(Event(it.body() as Transaction))
                        Log.d(javaClass.name, "success to add data: ")
                        //cancelJob()
                    }
                }
                else{
                    withContext(Main){
                        _newTransactionServerUiState.value = TestUiState.Error(Event(it.message()))
                        Log.d(javaClass.name, "onFailure 1: ${it.code()}")
                        //cancelJob()
                    }
                }

            }.onFailure {
                withContext(Main){
                    _newTransactionServerUiState.value = TestUiState.Error(Event(it.toString()))
                    Log.d(javaClass.name, "onFailure 2: ${it.message}")
                    //cancelJob()
                }
            }

        }

    }
    fun cancelNewServerJob(){
        newServerJob?.cancel()
    }

}