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
import com.example.mysimpleledger.R
import com.example.mysimpleledger.data.model.Transaction
import com.example.mysimpleledger.databinding.AddTransactionFormBinding
import com.example.mysimpleledger.databinding.FragmentAddTransactionBinding
import com.example.mysimpleledger.view.UiState
import com.example.mysimpleledger.view.view_model.TransactionViewModel
import com.example.mysimpleledger.utils.convertDateToServerFormat
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {

    lateinit var mFragmentAddTransactionBinding: FragmentAddTransactionBinding
    private val mTransactionViewModel: TransactionViewModel by viewModels()

    //var datePicker =
    ///var builder: MaterialDatePicker.Builder<*> = datePicker()

    private lateinit var datePicker: MaterialDatePicker<Long>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mFragmentAddTransactionBinding = FragmentAddTransactionBinding.inflate(
            inflater,
            container,
            false
        )
        // Inflate the layout for this fragment
        return mFragmentAddTransactionBinding.root
    }

    

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDatePicker()
        datePickerViewClickListener()
        setDateOnDateView(getCurrentDate())
        getAddTransactionForm().saveButton.setOnClickListener {
            saveButtonOnClick()
        }

        lifecycleScope.launchWhenCreated {
            mTransactionViewModel.newTransactionUiState.collect {
                Log.d(javaClass.name, "update data ")

                when(it){
                    is UiState.Success ->{
                        showToast("Successfully added")
                        Log.d(javaClass.name, "success to add data ${it.data[0].Amount}")
                        setProgressBarVisibility(View.GONE)
                    }
                    is UiState.Loading ->{
                        Log.d(javaClass.name, "adding data ")
                        setProgressBarVisibility(View.VISIBLE)
                    }
                    is UiState.Error ->{
                        showSnackBar(it.message)
                        Log.d(javaClass.name, "failed to add "+it.message)
                        setProgressBarVisibility(View.GONE)
                    }
                    else -> Unit

                }
            }

        }


    }
    private fun showToast(message: String){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    @InternalCoroutinesApi
    private fun saveButtonOnClick(){
        if(checkInputAndShowError()){
            var transactions: ArrayList<Transaction> = ArrayList()
            transactions.add(getTransaction())
            saveTransaction(transactions)
        }
    }
    @InternalCoroutinesApi
    private fun saveTransaction(transactions: List<Transaction>){
        lifecycleScope.launch{
            mTransactionViewModel.addTransaction(transactions)
//            mTransactionViewModel.newTransactionUiState.collect {
//                Log.d(javaClass.name, "update data ")
//
//                when(it){
//                    is UiState.Success ->{
//                        showSnackBar("Successfully added")
//                        Log.d(javaClass.name, "success to add data ${it.data[0].Amount}")
//                        setProgressBarVisibility(View.GONE)
//                    }
//                    is UiState.Loading ->{
//                        Log.d(javaClass.name, "adding data ")
//                        setProgressBarVisibility(View.VISIBLE)
//                    }
//                    is UiState.Error ->{
//                        showSnackBar("Failed to Successfully")
//                        Log.d(javaClass.name, "failde to add "+it.message)
//                        setProgressBarVisibility(View.GONE)
//                    }
//                    else -> Unit
//
//                }
//            }

        }
    }

    private fun showSnackBar(message: String){
        Snackbar.make(mFragmentAddTransactionBinding.root, message, Snackbar.LENGTH_LONG).show()
    }



    private fun datePickerViewClickListener(){
        getAddTransactionForm().dateLayout.setOnClickListener {
            run {
                showDatePicker()
                datePickerPositiveListener()
            }
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-M-yyyy")
        return sdf.format(Date())
    }

    private fun getDatePicker(): MaterialDatePicker<Long> {
        return MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .build()
    }

    private fun initDatePicker(){
        datePicker = getDatePicker()
    }
    private fun showDatePicker(){

        datePicker.show(childFragmentManager, "tag")
    }
    private fun datePickerPositiveListener(){
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)
            val date = "${calendar.get(Calendar.DAY_OF_MONTH)}-" +
                    "${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}"
            setDateOnDateView(date)
        }
    }
    private fun setDateOnDateView(date:String){
        mFragmentAddTransactionBinding.addTransactionForm.dateTextView.text = date
    }

    private fun getAddTransactionForm(): AddTransactionFormBinding =
            mFragmentAddTransactionBinding.addTransactionForm

    private fun showErrorInTextInput(layout: TextInputLayout, message: String){
        layout.error=message
    }
    private fun getAmount(): String{
        Log.d(javaClass.name, "get amount: "+getAddTransactionForm().amountTextField.toString())
        return getAddTransactionForm().amountTextField.text.toString()
    }
    private fun isAmountValid(): Boolean{
        return getAmount().isNotEmpty()
    }
    private fun getContact(): String{
        return getAddTransactionForm().contactNameTextField.text.toString()
    }
    private fun isContactValid():Boolean{
        return getContact().isNotEmpty()
    }
    private fun getDescription(): String{
        return getAddTransactionForm().descriptionTextField.text.toString()
    }
    private fun isDescriptionValid(): Boolean{
        return getDescription().isNotEmpty()
    }
    private fun getTransactionContactType(): Int =
            if(getAddTransactionForm().transactionTypeRadioGroup.checkedRadioButtonId== R.id.radioButtonSell){
                0
            }
            else 1
    private fun getTransactionStatus(): Int =
            when (getAddTransactionForm().transactionStatusRadioGroup.checkedRadioButtonId) {
                R.id.radioButtonComplete -> 0
                R.id.radioButtonDena -> 1
                else -> 2
            }
    private fun getDate(): String{
        return getAddTransactionForm().dateTextView.text.toString()
    }

    private fun checkInputAndShowError(): Boolean{
        if(!isAmountValid()){
            showErrorInTextInput(getAddTransactionForm().amountTextFieldLayout, "invalid input")
            return false
        }
        else if(!isContactValid()){
            showErrorInTextInput(getAddTransactionForm().contactNameTextFieldLayout, "invalid input")
            return false
        }
        else if(!isDescriptionValid()){
            showErrorInTextInput(getAddTransactionForm().descriptionTextFieldLayout, "invalid input")
            return false
        }
        return true
    }

    private fun setProgressBarVisibility(visibility: Int){
        mFragmentAddTransactionBinding.progressBar.visibility=visibility
    }

    private fun getTransaction(): Transaction{
        //var transaction = Transaction("33"," ", 3F, "ddd", "dd",1, 1, "dd" )

        return Transaction(convertDateToServerFormat(getDate()), getAmount().toInt().toFloat(), getDescription(), getContact(), getTransactionContactType(), getTransactionStatus())
    }



}