package com.example.mysimpleledger.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mysimpleledger.data.PrefManager
import com.example.mysimpleledger.view.adapter.TransactionRecyclerAdapter
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.view.view_model.TransactionViewModel
import com.example.mysimpleledger.databinding.FragmentTransactionListBinding
import com.example.mysimpleledger.view.TestUiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class TransactionListFragment : Fragment() {
    lateinit var mFragmentTransactionListBinding: FragmentTransactionListBinding
    lateinit var transactionRecyclerAdapter: TransactionRecyclerAdapter

    private val mTransactionViewModel: TransactionViewModel by viewModels()
    var job: Job? = null
    var dataGetJob: Job? = null

    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentTransactionListBinding = FragmentTransactionListBinding.inflate(
            inflater, container, false
        )
        return mFragmentTransactionListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if(savedInstanceState==null){
//            getTransaction()
//        }
//        else{
//            initAdapter()
//            initRecyclerView()
//        }
        swipeRefreshListener()

    }

    override fun onResume() {
        super.onResume()
        if(mTransactionViewModel.transactionList.value.isNullOrEmpty()){
            getTransaction()
        }
        else{
            initAdapter()
            initRecyclerView()
        }

    }

    private fun swipeRefreshListener(){
        mFragmentTransactionListBinding.swipeRefresh.setOnRefreshListener {
            getTransaction()
            mFragmentTransactionListBinding.swipeRefresh.isRefreshing = false
        }
    }

    private fun getTransaction() {
        dataGetJob?.cancel()
        dataGetJob = lifecycleScope.launch {
            mTransactionViewModel.getTransaction()
        }
        observeTransactionData(dataGetJob)


    }
    private fun showToast(message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun showEmptyState(){
        setProgressBarVisibility(View.GONE)
        setIsListEmptyVisibility(View.VISIBLE)
        setErrorMessageVisibility(View.GONE)
    }
    private fun saveServerTransaction(transaction: List<Transaction>){
        job?.cancel()
        job = Job()
        job?.let {
            lifecycleScope.launch {
                mTransactionViewModel.saveTransaction(transaction)
                mTransactionViewModel.saveTransactionUiState.collect {
                    when(it){
                        is TestUiState.Empty ->{
                            showEmptyState()
                        }
                        is TestUiState.Loading->{

                        }
                        is TestUiState.Success->{
                            prefManager.saveLastSyncDateTime()
                            getTransaction()
                        }
                    }
                }
            }
        }
    }

    private fun loadDataFromServer(){
        Log.d(javaClass.name, "loadDataFromServer")
        //job?.cancel()
        lifecycleScope.launch {
            mTransactionViewModel.getTransactionFromServer()
            mTransactionViewModel.transactionServerUiState.collect { uiState->
                when(uiState){
                    is TestUiState.Empty ->{
                        showEmptyState()
                        Log.d(javaClass.name, "got transaction from server empty")
                    }
                    is TestUiState.Loading->{
                        Log.d(javaClass.name, "got transaction from server loading")
                    }
                    is TestUiState.Success ->{
                        mTransactionViewModel.isLoadedFromServer = true
                        val data = uiState.data?.getContentIfNotHandled() as List<Transaction>
                        Log.d(javaClass.name, "got transaction from server success ${data.size}")

                        if(!data.isNullOrEmpty()){
                            saveServerTransaction(data)
                        }
                        else{
                            showEmptyState()
                        }
                    }
                }
            }
        }
    }

    private fun observeTransactionData(dataGetJob: Job?){
        job?.cancel()
        job = lifecycleScope.launchWhenCreated {
            mTransactionViewModel.transactionUiState.collect {uiState->
                Log.d(javaClass.name, "data collected in transaction frag $uiState")
                when (uiState) {
                    is TestUiState.Empty ->{
                        showEmptyState()
                    }
                    is TestUiState.Success -> {
                        val data = uiState.data?.getContentIfNotHandled()
                        if(data==null){
                            Log.d(javaClass.name, "data collected in 11")
                            if(mTransactionViewModel.transactionList.value==null|| mTransactionViewModel.transactionList.value!!.isEmpty()) {
                                showEmptyState()
                            }
                            else{
                                setProgressBarVisibility(View.GONE)
                                setIsListEmptyVisibility(View.GONE)
                                setErrorMessageVisibility(View.GONE)
                                initAdapter()
                                initRecyclerView()
                            }
                        }
                        else{
                            if((data as List<Transaction>).isEmpty()){
                                Log.d(javaClass.name, "empty list ${data.size}")
                                showEmptyState()
                                if(!mTransactionViewModel.isLoadedFromServer) loadDataFromServer()
                            }
                            else{
                                mTransactionViewModel.transactionListLiveDate.value = data
                                showToast("Data loaded")
                                setProgressBarVisibility(View.GONE)
                                setIsListEmptyVisibility(View.GONE)
                                setErrorMessageVisibility(View.GONE)
                                initAdapter()
                                initRecyclerView()
                            }
                        }
                        job?.cancel()
                        dataGetJob?.cancel()

                    }
                    is TestUiState.Loading -> {
                        Log.d(javaClass.name, "loading data ")
                        setProgressBarVisibility(View.VISIBLE)
                        setIsListEmptyVisibility(View.GONE)
                        setErrorMessageVisibility(View.GONE)
                    }
                    is TestUiState.Error -> {
                        showSnackBar("failed to load data")
                        Log.d(javaClass.name, "failed to add " + uiState.message)
                        setProgressBarVisibility(View.GONE)
                        setErrorMessageVisibility(View.VISIBLE)
                        setIsListEmptyVisibility(View.GONE)
                        job?.cancel()
                        dataGetJob?.cancel()
                        mTransactionViewModel.job?.cancel()

                    }
                }
            }
        }
    }
    private fun showSnackBar(message: String){
            Snackbar.make(mFragmentTransactionListBinding.root, message, Snackbar.LENGTH_LONG).show()
    }
    private fun setProgressBarVisibility(visibility: Int){
        mFragmentTransactionListBinding.progressBar.visibility=visibility
    }
    private fun setIsListEmptyVisibility(visibility: Int){
        mFragmentTransactionListBinding.isTransactionListEmpty.visibility=visibility
    }
    private fun setErrorMessageVisibility(visibility: Int){
        mFragmentTransactionListBinding.errorLoadingData.visibility=visibility
    }


    private fun initAdapter(){
        transactionRecyclerAdapter = TransactionRecyclerAdapter(
            mTransactionViewModel.transactionList.value,
            object : TransactionRecyclerAdapter.OnClickListener {
                override fun onItemClick(position: Int) {
                    Toast.makeText(context, "Item clicked $position", Toast.LENGTH_LONG).show()
                }

            }
        )
    }

    private fun initRecyclerView(){
        mFragmentTransactionListBinding.apply {
            this.transactionRecyclerView.apply {
                adapter = transactionRecyclerAdapter
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mTransactionViewModel.cancelJob()
    }



}