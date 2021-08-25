package com.example.mysimpleledger.ui.auth.login;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mysimpleledger.data.model.request.body.LoginBody
import com.example.mysimpleledger.data.model.request.body.RegistrationBody

import com.example.mysimpleledger.databinding.ActivityLoginBinding
import com.example.mysimpleledger.ui.TestUiState
import com.example.mysimpleledger.ui.auth.AuthViewModel
import com.example.mysimpleledger.ui.auth.registration.RegistrationActivity
import com.example.mysimpleledger.utils.showErrorInTextInputLayout
import com.example.mysimpleledger.utils.showSnackBar
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity: AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    private fun initView(){
        showTopBarBackButton()
        binding.apply {
            tvSignUp.setOnClickListener(this@LoginActivity)
            btSignIn.setOnClickListener(this@LoginActivity)
        }

    }
    private fun showTopBarBackButton(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                onBackPressed()
            }
        }
        return true
    }

    private fun getUserName(): String {
        return binding.etUserName.text.toString()
    }
    private fun isUserNameValid(): Boolean{
        return getUserName().isNotEmpty()
    }
    private fun getPassword(): String{
        return binding.etPassword.text.toString()
    }
    private fun isPasswordValid(): Boolean{
        return getPassword().isNotEmpty()
    }

    private fun getLoginBodyFromView(): LoginBody{
        return LoginBody(
                userName = getUserName(),
                password = getPassword()
        )
    }


    @InternalCoroutinesApi
    private fun signInClick(){
        if(!isUserNameValid()){
            showErrorInTextInputLayout(binding.tilUserName, "Required field")
            return
        }
        if(!isPasswordValid()){
            showErrorInTextInputLayout(binding.tilPassword, "Required field")
            return
        }
        loginObserve(getLoginBodyFromView())
    }

    @InternalCoroutinesApi
    private fun loginObserve(loginBody: LoginBody){
        lifecycleScope.launch {
            viewModel.login(loginBody)
            viewModel.loginDataState.collect {uiState->
                when (uiState) {
                    is TestUiState.Empty ->{
                    }
                    is TestUiState.Success -> {
                        val data = uiState.data?.getContentIfNotHandled()
                        if(data==null){
                            Log.d(javaClass.name, "data collected in 11")

                        }
                        else{
                            showSnackBar(binding.root, "Successfully Login")
                        }
                    }
                    is TestUiState.Loading -> {
                        Log.d(javaClass.name, "loading data ")

                    }
                    is TestUiState.Error -> {
                        Log.d(javaClass.name, "failed to loign " + uiState.message?.getContentIfNotHandled())
                        showSnackBar(binding.root, "Failed to Login")

                    }
                }
            }

        }
    }


    @InternalCoroutinesApi
    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.btSignIn.id->{
                signInClick()
            }
            binding.tvSignUp.id->{
                startActivity(Intent(this, RegistrationActivity::class.java))
            }
        }
    }
}