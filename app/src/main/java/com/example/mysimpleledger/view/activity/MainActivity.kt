package com.example.mysimpleledger.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.mysimpleledger.R
import com.example.mysimpleledger.data.PrefManager
import com.example.mysimpleledger.databinding.ActivityMainBinding
import com.example.mysimpleledger.view.auth.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var mActivityMainBinding: ActivityMainBinding
    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityMainBinding.root)

        initNavController()
        initNavigation()
        fragmentPageChangeListener()



    }

    private fun showTopBarBackButton(){
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }
    private fun hideTopBarBackButton(){
        supportActionBar?.setDisplayHomeAsUpEnabled(false);
        supportActionBar?.setDisplayShowHomeEnabled(false);
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        initTopBarMenu(menu)
        return true
    }

    private fun fragmentPageChangeListener(){
        navController.addOnDestinationChangedListener { navController: NavController, navDestination: NavDestination, bundle: Bundle? ->
            when (navDestination.id){
                R.id.transactionListFragment ->{
                    setVisibilityOfBottomNavigationView(View.VISIBLE)
                    showToolBar()
                }
                R.id.addTransactionFragment ->{
                    setVisibilityOfBottomNavigationView(View.VISIBLE)
                    showToolBar()
                }

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings ->{
                showSettingsPage()
            }
            android.R.id.home ->{
                onBackPressed()
            }
        }
        return true
    }

    private fun showToolBar(){
        supportActionBar?.show()
    }
    private fun hideToolBar(){
        supportActionBar?.hide()
    }

    private fun showSettingsPage(){
        setVisibilityOfBottomNavigationView(View.GONE)
        hideToolBar()
        if(getCurrentDestinationId()==R.id.addTransactionFragment){
            navigateToAnotherPage(R.id.action_addTransactionFragment_to_settingsFragment)
        }
        else if(getCurrentDestinationId()==R.id.transactionListFragment){
            navigateToAnotherPage(R.id.action_transactionListFragment_to_settingsFragment)
        }


    }

    private fun navigateToAnotherPage(action: Int){
        navController.navigate(action)
    }

    private fun getCurrentDestinationId(): Int?{
        return navController.currentDestination?.id
    }

    private fun setVisibilityOfBottomNavigationView(visibility: Int){
        mActivityMainBinding.homeBottomNav.visibility = visibility
    }

    private fun initTopBarMenu(menu: Menu?){
        menuInflater.inflate(R.menu.top_right_menu, menu)

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

    override fun onResume() {
        super.onResume()
        Log.d(javaClass.name, "token ${prefManager.getToken()}")
        if(prefManager.getToken().isEmpty()){
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

}