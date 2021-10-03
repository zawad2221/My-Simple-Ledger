package com.example.mysimpleledger.view.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.example.mysimpleledger.data.PrefManager
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.databinding.FragmentSettingsBinding
import com.example.mysimpleledger.services.SyncWorker
import com.example.mysimpleledger.utils.makeGone
import com.example.mysimpleledger.utils.makeInVisible
import com.example.mysimpleledger.utils.makeVisible
import com.example.mysimpleledger.view.TestUiState
import com.example.mysimpleledger.view.view_model.TransactionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import com.example.mysimpleledger.R


@AndroidEntryPoint
class SettingsFragment : Fragment() {
   private lateinit var mFragmentSettingsBinding: FragmentSettingsBinding
    private val mTransactionViewModel: TransactionViewModel by viewModels()

   @Inject
   lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mFragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return mFragmentSettingsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        mFragmentSettingsBinding.toolBar.setNavigationOnClickListener {
            onBackPress()
        }
        mFragmentSettingsBinding.backupNow.setOnClickListener {
            //val work = OneTimeWorkRequest.from(SyncWorker::class.java)
            val work = OneTimeWorkRequest.Builder(SyncWorker::class.java)
                .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build())
                    .build()

            // provide custom configuration
            val myConfig = Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.INFO)
                    .build()

            // initialize WorkManager
            //activity?.applicationContext?.let { it1 -> WorkManager.initialize(it1, myConfig) }
            activity?.let { it1 -> WorkManager.getInstance(it1.applicationContext).beginWith(work).enqueue() }
        }
        observeNewTransaction()
    }

    private fun observeNewTransaction(){
        lifecycleScope.launchWhenCreated {
            mTransactionViewModel.getNewTransactionFromOffline()
            mTransactionViewModel.newAllTransactionUiState.collect {
                when(it){
                    is TestUiState.Success->{
                        val data = it.data?.getContentIfNotHandled() as List<Transaction>
                        if(!data.isNullOrEmpty()){
                            Log.d(TAG, "data backup status: not all backup")
                            mFragmentSettingsBinding.apply {
                                backupStatus.text = resources.getString(R.string.backup_status_not_backup)
                                ivBackup.setImageResource(R.drawable.cloud_not_backup)
                            }

                        }
                        else{
                            Log.d(TAG, "data backup status:all backup")
                            mFragmentSettingsBinding.apply {
                                backupStatus.text = resources.getString(R.string.backup_status_backup)
                                ivBackup.setImageResource(R.drawable.cloud_backup_done)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initView(){
        with(mFragmentSettingsBinding){
            if(prefManager.getLastSyncDateTime().isEmpty()){
                tvLastSync.makeGone()
                tvLastSyncLable.makeGone()
            }
            else{
                tvLastSyncLable.makeVisible()
                tvLastSync.makeVisible()
                tvLastSync.text = prefManager.getLastSyncDateTime()
            }
        }
    }

    private fun onBackPress(){
        activity?.onBackPressed()
    }


}