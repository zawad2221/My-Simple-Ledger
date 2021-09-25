package com.example.mysimpleledger.view.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.repository.TransactionRepository
import com.example.mysimpleledger.view.TestUiState
import com.example.mysimpleledger.view.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val transactionRepository: TransactionRepository): ViewModel() {

    var isLoadedFromServer = false
    var job: Job? = null

    val transactionListLiveDate = MutableLiveData<List<Transaction>>()
    val transactionList: LiveData<List<Transaction>> = transactionListLiveDate

    //add transaction data
    private val _newTransactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val newTransactionUiState: StateFlow<UiState> = _newTransactionUiState
    private val _saveTransactionUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val saveTransactionUiState: StateFlow<TestUiState> = _saveTransactionUiState

    //transaction data
    private val _transactionUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionUiState: StateFlow<TestUiState> = _transactionUiState


    //server data
    private val _transactionServerUiState = MutableStateFlow<TestUiState>(TestUiState.Empty)
    val transactionServerUiState: StateFlow<TestUiState> = _transactionServerUiState

    @InternalCoroutinesApi
    suspend fun addTransaction(transactions: List<Transaction>){
        viewModelScope.launch {
            transactionRepository.saveTransaction(transactions)
            Log.d(javaClass.name, "save transaction in view model")

        }

        transactionRepository.newTransactionUiState.collect {
            _newTransactionUiState.value=it
            Log.d(javaClass.name, "transaction add state update"+it.toString())

        }
    }

    suspend fun saveTransaction(transactions: List<Transaction>){
        viewModelScope.launch {
            transactionRepository.saveTransactions(transactions)
            transactionRepository.addTransactionUiState.collect {
                _saveTransactionUiState.value = it
            }
        }
    }

    suspend fun getTransactionFromServer(){

        viewModelScope.launch {
            transactionRepository.getTransactionByUserId()
            transactionRepository.transactionServerUiState.collect{
                _transactionServerUiState.value = it
            }
        }

    }

    suspend fun getTransaction(){
        job = viewModelScope.launch {
            transactionRepository.getAllOfflineTransaction()
            transactionRepository.transactionUiStateTest.collect{
                Log.d(javaClass.name, "got data update $it")
                _transactionUiState.value = it
                when(it){
                    is TestUiState.Success, is TestUiState.Error->{
                        //job?.cancel()

                    }
                }


            }
        }



    }

    fun cancelJob(){
        transactionRepository.cancelServerJob()
    }

}