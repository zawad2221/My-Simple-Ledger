package com.example.mysimpleledger.ui.fragment

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
import com.example.mysimpleledger.adapter.TransactionRecyclerAdapter
import com.example.mysimpleledger.ui.view_model.TransactionViewModel
import com.example.mysimpleledger.databinding.FragmentTransactionListBinding
import com.example.mysimpleledger.ui.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TransactionListFragment : Fragment() {
    lateinit var mFragmentTransactionListBinding: FragmentTransactionListBinding
    lateinit var transactionRecyclerAdapter: TransactionRecyclerAdapter

    private val mTransactionViewModel: TransactionViewModel by viewModels()

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
        getTransaction()
        observeTransactionData()
        swipeRefreshListener()

    }

    private fun swipeRefreshListener(){
        mFragmentTransactionListBinding.swipeRefresh.setOnRefreshListener {
            getTransaction()
            mFragmentTransactionListBinding.swipeRefresh.isRefreshing = false
        }
    }

    private fun getTransaction() {
        lifecycleScope.launch {
            mTransactionViewModel.getTransaction()

        }

    }
    private fun showToast(message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
    private fun observeTransactionData(){
        lifecycleScope.launchWhenCreated {
            mTransactionViewModel.transactionUiState.collect {
                when (it) {
                    is UiState.Empty ->{
                        setProgressBarVisibility(View.GONE)
                        setIsListEmptyVisibility(View.VISIBLE)
                        setErrorMessageVisibility(View.GONE)
                    }
                    is UiState.Success -> {
                        showToast("Data loaded")
                        Log.d(javaClass.name, "success to load data ${it.data[0].Amount}")
                        setProgressBarVisibility(View.GONE)
                        setIsListEmptyVisibility(View.GONE)
                        setErrorMessageVisibility(View.GONE)
                        initAdapter()
                        initRecyclerView()
                    }
                    is UiState.Loading -> {
                        Log.d(javaClass.name, "loading data ")
                        setProgressBarVisibility(View.VISIBLE)
                        setIsListEmptyVisibility(View.GONE)
                        setErrorMessageVisibility(View.GONE)
                    }
                    is UiState.Error -> {
                        showSnackBar("Failed to load data")
                        Log.d(javaClass.name, "failed to add " + it.message)
                        setProgressBarVisibility(View.GONE)
                        setErrorMessageVisibility(View.VISIBLE)
                        setIsListEmptyVisibility(View.GONE)

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



}