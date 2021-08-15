package com.example.mysimpleledger.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.model.request.TransactionBody
import com.example.mysimpleledger.data.repository.TransactionRepository
import com.example.mysimpleledger.ui.TestUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(appContext, workerParams) {
    var job: Job?=null
    init {

    }
    @DelicateCoroutinesApi
    override suspend fun doWork(): Result {
        Log.d(javaClass.name, "worker running $transactionRepository")
        getNewTransactionFromOffline()

        return Result.success()
    }

    private suspend fun getNewTransactionFromOffline(){
        var offlineJob: Job? = null
        offlineJob = Job()
        transactionRepository.getAllNewTransaction()
        offlineJob = CoroutineScope(Dispatchers.IO).launch {
            transactionRepository.newAllTransactionUiState.collect{
                Log.d(javaClass.name, "got data update $it")
                when(it){
                    is TestUiState.Success->{
                        it.data.let { data->
                            val result = data?.getContentIfNotHandled()
                            result?.let { notNullResult->
                                notNullResult as List<Transaction>
                                Log.d(javaClass.name, " ...............worker fetch sqlite data: "+ notNullResult[0].toString())
                                notNullResult[0].Amount+=1
                                saveTransactionInServer(notNullResult)

                            }
                        }

                    }
                    else -> Unit
                }

            }
        }
    }

    private fun getTransactionBody(transaction: Transaction): TransactionBody{
        return TransactionBody(
                Amount = transaction.Amount,
                UserId = transaction.UserId,
                Contact = transaction.Contact,
                Date = transaction.Date,
                Description = transaction.Description
        )
    }

    private suspend fun saveTransactionInServer(transactions: List<Transaction>){
        var serverJob: Job?=null
        transactions[0].UserId = "rifat.rz@gmail.com"
        Log.d("MSL", "transaction: ${transactions[0].toString()}")
        transactionRepository.saveDataInServer(transactions[0])
        serverJob = CoroutineScope(Dispatchers.IO).launch {
            transactionRepository.newTransactionServerUiState.collect {
                when(it){
                    is TestUiState.Success->{
                        it.data?.getContentIfNotHandled()?.let {
                            (it as Transaction).Amount = 91f
                            Log.d(javaClass.name, "MSL: seved response ${it.toString()}")
                            setIsBackup(it, true)
                            setOffLineId(it, transactions[0]?.offlineId)
                            setDate(it, transactions[0].Date)
                            updateTransaction(listOf(it))
                        }
                    }
                }
            }
        }

    }

    private fun setOffLineId(transaction: Transaction, offlineId: Int?): Transaction{
        transaction.offlineId = offlineId
        return transaction
    }
    private fun setIsBackup(transaction: Transaction, isBackup: Boolean): Transaction {
        transaction.isBackup = isBackup
        return transaction
    }
    private fun setDate(transaction: Transaction, date: String): Transaction {
        transaction.Date = date
        return transaction
    }


    private suspend fun getTransactionFromServer(){
        transactionRepository.getTransactionByUserId("rifat.rz@gmail.com")
        var serverJob: Job? = null
        serverJob = Job()
        serverJob = CoroutineScope(Dispatchers.IO).launch {
            transactionRepository.transactionServerUiState.collect{
                when(it){
                    is TestUiState.Success->{
                        it.data?.getContentIfNotHandled()?.let {
                            Log.d(javaClass.name, "data got from server ${(it as List<Transaction>).size}")
                            serverJob?.cancel()

                        }
                    }
                    is TestUiState.Error->{
                        serverJob?.cancel()

                    }
                }
            }
        }
    }



    @DelicateCoroutinesApi
    private suspend fun updateTransaction(transactions:List<Transaction>){
        transactionRepository.updateTransactions(transactions)
        var updateInOfflineJob: Job? = null
        updateInOfflineJob = Job()
        updateInOfflineJob = GlobalScope.launch {
            transactionRepository.transactionUpdateUiState.collect {
                Log.d(javaClass.name, "got updated data update $it")
                when (it) {
                    is TestUiState.Success -> {
                        it.data.let { data ->
                            val result = data?.getContentIfNotHandled()
                            result?.let { notNullResult ->
                                notNullResult as List<Transaction>
                                Log.d(javaClass.name, " ...............worker fetch updated sqlite data: " + notNullResult[0].Id)

                            }
                        }

                    }
                    else -> Unit
                }

            }
        }
    }
}