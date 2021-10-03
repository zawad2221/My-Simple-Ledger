package com.example.mysimpleledger.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mysimpleledger.data.PrefManager
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.repository.TransactionRepository
import com.example.mysimpleledger.view.TestUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(appContext, workerParams) {



    private var numberOfDataToBackup: Int = 0
    @Inject
    lateinit var prefManager: PrefManager
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
                                if(notNullResult.isEmpty()){
                                    Log.d(javaClass.name, "no new data got")
                                }
                                else{
                                    numberOfDataToBackup = notNullResult.size
                                    Log.d(javaClass.name, " ...............worker fetch sqlite data: "+ notNullResult[0].toString())
                                    for(transaction in notNullResult){
                                        saveTransactionInServer(transaction)
                                    }
                                    Log.d(javaClass.name,"000000000000000000000000000000000000000000000000000000000")

                                }



                            }
                        }

                    }
                    else -> Unit
                }

            }
        }
    }



    private suspend fun saveTransactionInServer(transaction: Transaction){
        Log.d(javaClass.name,"###############################################################################")
        var serverJob: Job?=null
        transaction.UserId = "rifat.rz@gmail.com"
        Log.d("MSL", "transaction: ${transaction.toString()}")
        transactionRepository.saveDataInServer(transaction)
        serverJob = CoroutineScope(Dispatchers.IO).launch {
            transactionRepository.newTransactionServerUiState.collect {
                when(it){
                    is TestUiState.Success->{
                        it.data?.getContentIfNotHandled()?.let {
                            (it as Transaction).Amount = 91f
                            Log.d(javaClass.name, "MSL: seved response ${it.toString()}")
                            setIsBackup(it, true)
                            setOffLineId(it, transaction?.offlineId)
                            setDate(it, transaction.Date)
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
        transactionRepository.getTransactionByUserId()
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
                        prefManager.saveLastSyncDateTime()
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