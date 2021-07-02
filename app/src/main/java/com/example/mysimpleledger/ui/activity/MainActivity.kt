package com.example.mysimpleledger.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mysimpleledger.R
import com.example.mysimpleledger.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var mActivityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)

        initNavController()
        initNavigation()
    }

    private fun initNavController(){
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.homeFragmentHolder) as NavHostFragment
        navController = navHostFragment.navController
    }
    private fun initNavigation(){
        NavigationUI.setupWithNavController(
            mActivityMainBinding.homeBottomNav,
            navController
        )
    }
}