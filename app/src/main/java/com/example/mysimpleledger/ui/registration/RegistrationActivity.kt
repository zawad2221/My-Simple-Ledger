package com.example.mysimpleledger.ui.registration;

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem
import android.view.View

import com.example.mysimpleledger.R;
import com.example.mysimpleledger.databinding.ActivityRegistrationBinding

public class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }
    private fun initView(){
        showTopBarBackButton()
        binding.apply {

        }
    }
    private fun showTopBarBackButton(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                finish()
            }
        }
        return true
    }

}