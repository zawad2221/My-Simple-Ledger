package com.example.mysimpleledger.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mysimpleledger.data.repository.TransactionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        Log.d(javaClass.name, "worker running $transactionRepository")
        return Result.success()
    }
}