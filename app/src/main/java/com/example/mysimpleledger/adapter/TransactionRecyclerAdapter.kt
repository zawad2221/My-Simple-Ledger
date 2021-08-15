package com.example.mysimpleledger.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mysimpleledger.databinding.TransactionItemBinding
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.utils.convertDateToUIFormat

class TransactionRecyclerAdapter(
    private var transactionList: List<Transaction>?,
    private var onClickListener: OnClickListener
): RecyclerView.Adapter<TransactionRecyclerAdapter.TransactionViewHolder>() {


    class TransactionViewHolder(val transactionItemBinding: TransactionItemBinding) : RecyclerView.ViewHolder(transactionItemBinding.root) {

    }
    interface OnClickListener{
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList?.get(position)
        transaction?.Date = convertDateToUIFormat(transaction!!.Date)
        holder.transactionItemBinding.transaction= transaction
        holder.transactionItemBinding.transactionItemCardView.setOnClickListener {
            onClickListener.onItemClick(position)
        }

    }

    override fun getItemCount(): Int {
        return transactionList?.size ?: 0
    }
}