package com.example.mysimpleledger.view.auth.registration.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.mysimpleledger.R
import com.example.mysimpleledger.data.model.request.body.RegistrationBody
import com.example.mysimpleledger.databinding.FragmentRegistrationFormBinding
import com.example.mysimpleledger.view.TestUiState
import com.example.mysimpleledger.view.auth.AuthViewModel
import com.example.mysimpleledger.view.auth.login.LoginActivity
import com.example.mysimpleledger.utils.showErrorInTextInputLayout
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFormFragment :
    Fragment(), View.OnClickListener {
    private lateinit var navController: NavController
    private var _binding: FragmentRegistrationFormBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNavController()
        initView()
    }
    private fun initView(){
        binding.apply {
            makePasswordConditionNormal(tvPasswordCondition1)
            makePasswordConditionNormal(tvPasswordCondition2)
            makePasswordConditionNormal(tvPasswordCondition3)
            btSignUp.setOnClickListener(this@RegistrationFormFragment)
            tvSignIn.setOnClickListener(this@RegistrationFormFragment)
        }
    }
    private fun initNavController(){
        navController = findNavController()
    }

    private fun setDrawableStartInTextView(textView: TextView, drawable: Drawable?){
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    }

    private fun getColor(color: Int):Int{
        return ContextCompat.getColor(requireContext(), color)
    }

    private fun makePasswordConditionRed(textView: TextView){
        textView.setTextColor(getColor(R.color.colorOrange))
        setDrawableStartInTextView(textView, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_orange_cross))
    }
    private fun makePasswordConditionGreen(textView: TextView){
        textView.setTextColor(getColor(R.color.midGreen))
        setDrawableStartInTextView(textView, AppCompatResources.getDrawable(requireContext(), R.drawable.ic_check_circle))
    }
    private fun makePasswordConditionNormal(textView: TextView){
        textView.setTextColor(getColor(R.color.colorTextLight))
        setDrawableStartInTextView(textView, AppCompatResources.getDrawable(requireContext(), R.drawable.silver_circle))
    }
    private fun isPassGreaterEq8Char(pass: String): Boolean{
        return pass.length>=8
    }
    private fun isPassHasUpperLowerCase(pass: String): Boolean{
        val regex = ("^(?=.*[a-z])(?=."
                + "*[A-Z])"
                + ".+$")
        return (pass.matches(regex.toRegex()))
    }
    private fun isPassHasNumber(pass: String): Boolean{
        val regex = ("^(?=.*\\d)"
                + "(?=.*[-+_!@#\$%^&*., ?]).+\$")
        return (pass.matches(regex.toRegex()))
    }

    private fun getEmail():String{
        return binding.etEmail.text.toString()
    }
    private fun isValidEmail():Boolean{
        return getEmail().isNotEmpty() && getEmail().contains("@")
    }
    private fun getPassword(): String{
        return binding.etPassword.text.toString()
    }
    private fun showSnackBar(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    @InternalCoroutinesApi
    private fun signUpClick(){
        var valid = true
        if(!isValidEmail()){
            showErrorInTextInputLayout(binding.tilEmail, "Invalid Input")
            valid = false
        }
        else{
            showErrorInTextInputLayout(binding.tilEmail, null)
        }
        if(getPassword().isEmpty()){
            showErrorInTextInputLayout(binding.tilPassword, "Can not be empty")
            valid = false
        }
        if(!isPassGreaterEq8Char(getPassword())){
            makePasswordConditionRed(binding.tvPasswordCondition1)
            valid = false
        }
        else{
            makePasswordConditionGreen(binding.tvPasswordCondition1)
        }
        if(!isPassHasUpperLowerCase(getPassword()) ){
            makePasswordConditionRed(binding.tvPasswordCondition2)
            valid = false
        }
        else{
            makePasswordConditionGreen(binding.tvPasswordCondition2)
        }
        if(!isPassHasNumber(getPassword()) ){
            makePasswordConditionRed(binding.tvPasswordCondition3)
            valid = false
        }
        else{
            makePasswordConditionGreen(binding.tvPasswordCondition3)
        }
        if(!valid){
            return
        }
        regObserve(getRegBodyFromView())
    }
    @InternalCoroutinesApi
    private fun regObserve(registrationBody: RegistrationBody){
        lifecycleScope.launch {
            viewModel.registration(registrationBody)
            viewModel.registrationDataState.collect {uiState->
                when (uiState) {
                    is TestUiState.Empty ->{
                    }
                    is TestUiState.Success -> {
                        val data = uiState.data?.getContentIfNotHandled()
                        if(data==null){
                            Log.d(javaClass.name, "data collected in 11")

                        }
                        else{
                            showSnackBar("Successfully Register")
                            requireActivity().finish()
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                        }
                    }
                    is TestUiState.Loading -> {
                        Log.d(javaClass.name, "loading data ")

                    }
                    is TestUiState.Error -> {
                        Log.d(javaClass.name, "failed to add " + uiState.message?.getContentIfNotHandled())
                        showSnackBar("Failed to Register")

                    }
                }
            }

        }
    }
    private fun getRegBodyFromView(): RegistrationBody{
        return RegistrationBody(
                UserName = getEmail(),
                Email = getEmail(),
                Password = getPassword()
        )
    }


    @InternalCoroutinesApi
    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.btSignUp.id ->{
                signUpClick()
                //navController.navigate(R.id.action_registrationFormFragment_to_otpCodeFragment)
            }
            binding.tvSignIn.id->{
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }
}