package com.example.mysimpleledger.ui.login;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem
import android.view.View

import com.example.mysimpleledger.R;
import com.example.mysimpleledger.databinding.ActivityLoginBinding
import com.example.mysimpleledger.ui.registration.RegistrationActivity

class LoginActivity: AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding

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

    override fun onClick(p0: View?) {
        when(p0?.id){
            binding.tvSignUp.id->{
                startActivity(Intent(this, RegistrationActivity::class.java))
            }
        }
    }
}