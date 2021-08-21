package com.example.mysimpleledger.ui.auth.registration.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.mysimpleledger.R
import com.example.mysimpleledger.databinding.FragmentRegistrationFormBinding
import com.example.mysimpleledger.ui.auth.login.LoginActivity


class RegistrationFormFragment :
    Fragment(), View.OnClickListener {
    private lateinit var navController: NavController
    private var _binding: FragmentRegistrationFormBinding? = null
    private val binding get() = _binding!!

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
            btSignUp.setOnClickListener(this@RegistrationFormFragment)
            tvSignIn.setOnClickListener(this@RegistrationFormFragment)
        }
    }
    private fun initNavController(){
        navController = findNavController()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.btSignUp.id ->{
                navController.navigate(R.id.action_registrationFormFragment_to_otpCodeFragment)
            }
            binding.tvSignIn.id->{
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }
    }
}