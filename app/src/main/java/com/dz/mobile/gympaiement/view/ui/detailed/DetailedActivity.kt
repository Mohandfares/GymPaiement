package com.dz.mobile.gympaiement.view.ui.detailed

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment


import com.dz.mobile.gympaiement.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0f
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.nav_graph_dtailed, intent.extras)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}