package com.example.mysimpleledger.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.work.*
import com.example.mysimpleledger.data.PrefManager
import com.example.mysimpleledger.databinding.FragmentSettingsBinding
import com.example.mysimpleledger.services.SyncWorker
import com.example.mysimpleledger.utils.makeInVisible
import com.example.mysimpleledger.utils.makeVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
   private lateinit var mFragmentSettingsBinding: FragmentSettingsBinding

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
    }

    private fun initView(){
        with(mFragmentSettingsBinding){
            if(prefManager.getLastSyncDateTime().isEmpty()){
                tvLastSync.makeInVisible()
            }
            else{
                tvLastSync.makeVisible()
                tvLastSync.text = prefManager.getLastSyncDateTime()
            }
        }
    }

    private fun onBackPress(){
        activity?.onBackPressed()
    }


}