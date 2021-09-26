package com.example.mysimpleledger.view.auth.login;

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
import com.example.mysimpleledger.utils.*
import com.example.mysimpleledger.view.TestUiState
import com.example.mysimpleledger.view.activity.MainActivity
import com.example.mysimpleledger.view.auth.AuthViewModel
import com.example.mysimpleledger.view.auth.registration.RegistrationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity: AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

    private val viewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var errorHandler: ErrorHandler
    @Inject
    lateinit var snackbarHandler: SnackbarHandler

    var job: Job?= null

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
        val loginBody = getLoginBodyFromView()
        prefManager.saveUserEmail(loginBody.userName!!)
        loginObserve(loginBody)
    }

    private fun showProgressDialog(){
        setVisibilityOfView(listOf(binding.progressBar), View.VISIBLE)
    }
    private fun hideProgressDialog(){
        setVisibilityOfView(listOf(binding.progressBar), View.GONE)
    }

    @InternalCoroutinesApi
    private fun loginObserve(loginBody: LoginBody){
        viewModel.cancelJob()
        job?.cancel()
        job = lifecycleScope.launch {
            viewModel.login(loginBody)
            viewModel.loginDataState.collect {uiState->
                when (uiState) {
                    is TestUiState.Empty ->{
                    }
                    is TestUiState.Success -> {
                        hideProgressDialog()
                        binding.root.enable(true)
                        val data = uiState.data?.getContentIfNotHandled() as LoginResponse
                        if(data==null){
                            Log.d(javaClass.name, "data collected in 11")

                        }
                        else{
                            data.token?.let { prefManager.saveToken(it) }

                            snackbarHandler.showSuccess(this@LoginActivity)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                    is TestUiState.Loading -> {
                        Log.d(javaClass.name, "loading data ")
                        showProgressDialog()
                        binding.root.enable(false)

                    }
                    is TestUiState.Error -> {
                        hideProgressDialog()
                        binding.root.enable(true)
                        prefManager.clearEmail()
                        Log.d(javaClass.name, "failed to loign " + uiState.message?.getContentIfNotHandled())
//                        showSnackBar(binding.root, "Failed to Login")
                        errorHandler.handleError(errorHandler.parse(Int.MAX_VALUE), this@LoginActivity)

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