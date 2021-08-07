package com.example.mysimpleledger.services

import android.content.Context
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.data.repository.TransactionRepository
import com.example.mysimpleledger.ui.TestUiState
import com.example.mysimpleledger.ui.UiState
import com.example.mysimpleledger.ui.view_model.TransactionViewModel
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
        transactionRepository.getAllTransactionTest()
        job = GlobalScope.launch {
            transactionRepository.transactionUiStateTest.collect{
                Log.d(javaClass.name, "got data update $it")
                when(it){
                    is TestUiState.Success->{
                        it.data.let { data->
                            val result = data?.getContentIfNotHandled()
                            result?.let { notNullResult->
                                notNullResult as List<Transaction>
                                Log.d(javaClass.name, " ...............worker fetch sqlite data: "+ notNullResult[0].Id)
                                notNullResult[0].Amount+=1
                                updateTransaction(notNullResult)
                                job?.cancel()
                            }
                        }

                    }
                    else -> Unit
                }

            }
        }

        return Result.success()
    }



    @DelicateCoroutinesApi
    private suspend fun updateTransaction(transactions:List<Transaction>){
        transactionRepository.updateTransactions(transactions)
        job = GlobalScope.launch {
            transactionRepository.transactionUpdateUiState.collect {
                Log.d(javaClass.name, "got updated data update $it")
                when (it) {
                    is TestUiState.Success -> {
                        it.data.let { data ->
                            val result = data?.getContentIfNotHandled()
                            result?.let { notNullResult ->
                                notNullResult as List<Transaction>
                                Log.d(javaClass.name, " ...............worker fetch updated sqlite data: " + notNullResult[0].Id)
                                job?.cancel()
                            }
                        }

                    }
                    else -> Unit
                }

            }
        }
    }
}