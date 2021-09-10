package com.example.mysimpleledger.ui.auth.login;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mysimpleledger.data.PrefManager
import com.example.mysimpleledger.data.model.request.body.LoginBody
import com.example.mysimpleledger.data.model.request.response.LoginResponse

import com.example.mysimpleledger.databinding.ActivityLoginBinding
import com.example.mysimpleledger.ui.TestUiState
import com.example.mysimpleledger.ui.auth.AuthViewModel
import com.example.mysimpleledger.ui.auth.registration.RegistrationActivity
import com.example.mysimpleledger.utils.showErrorInTextInputLayout
import com.example.mysimpleledger.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity: AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var prefManager: PrefManager

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

    private fun getEmail(): String {
        return binding.etEmail.text.toString()
    }
    private fun isEmailValid(): Boolean{
        return (getEmail().isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches())
    }
    private fun getPassword(): String{
        return binding.etPassword.text.toString()
    }
    private fun isPasswordValid(): Boolean{
        return getPassword().isNotEmpty()
    }

    private fun getLoginBodyFromView(): LoginBody{
        return LoginBody(
                userName = getEmail(),
                password = getPassword()
        )
    }


    @InternalCoroutinesApi
    private fun signInClick(){
        if(!isEmailValid()){
            showErrorInTextInputLayout(binding.tilEmail, "Required field")
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
        viewModel.cancelJob()
        lifecycleScope.launch {
            viewModel.login(loginBody)
            viewModel.loginDataState.collect {uiState->
                when (uiState) {
                    is TestUiState.Empty ->{
                    }
                    is TestUiState.Success -> {
                        val data = uiState.data?.getContentIfNotHandled() as LoginResponse
                        if(data==null){
                            Log.d(javaClass.name, "data collected in 11")

                        }
                        else{
                            data.token?.let { prefManager.saveToken(it) }
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.cancelJob()
    }
}