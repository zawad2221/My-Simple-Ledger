package com.example.mysimpleledger.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.repository.TransactionRepository
import com.example.mysimpleledger.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.Util
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val transactionRepository: TransactionRepository): ViewModel() {



    private val transactionListLiveDate = MutableLiveData<List<Transaction>>()
    val transactionList: LiveData<List<Transaction>> = transactionListLiveDate

    //add transaction data
    private val _newTransactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val newTransactionUiState: StateFlow<UiState> = _newTransactionUiState

    //transaction data
    private val _transactionUiState = MutableStateFlow<UiState>(UiState.Empty)
    val transactionUiState: StateFlow<UiState> = _transactionUiState

    fun fakeData(){
//        val transaction: Transaction = Transaction(1222,null,"2021-06-18",1000.0F,"Complete transaction","Rfiat",0,0,"rifat@gmail.com",null)
//        val transaction1: Transaction = Transaction(1212,null,"2021-06-18",1000.0F,"Dena transaction","Zawad",1,1,"zawad@gmail.com",null)
//        val transaction2: Transaction = Transaction(1212,null,"2021-06-18",1000.0F,"Paona transaction","Hossain",1,2,"hossain@gmail.com",null)
//        val transaction3: Transaction = Transaction(1212,null,"2021-06-18",1000.0F,"Complete transaction","Rfiat",0,0,"rifat@gmail.com",null)
//        val transaction4: Transaction = Transaction(1212,null,"2021-06-18",1000.0F,"Dena transaction","Zawad",1,1,"zawad@gmail.com",null)
//        val transaction5: Transaction = Transaction(1212,null,"2021-06-18",1000.0F,"Paona transaction","Hossain",1,2,"hossain@gmail.com",null)
//        val tl: List<Transaction> = listOf(transaction, transaction1, transaction2, transaction3, transaction4, transaction5)
//        transactionListLiveDate.value = tl
    }

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

    suspend fun getTransaction(){
        viewModelScope.launch {
            transactionRepository.getAllTransaction()
        }
        transactionRepository.transactionUiState.collect{
            Log.d(javaClass.name, "got data update $it")
            _transactionUiState.value = it
            when(it){
                is UiState.Success->{
                    transactionListLiveDate.value = it.data
                }
                else -> Unit
            }
        }
    }

}