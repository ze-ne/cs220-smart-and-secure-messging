package com.cs220.ssmessaging.frontend.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.cs220.ssmessaging.R
import com.cs220.ssmessaging.frontend.adapters.HomeTabAdapter
import com.google.android.material.tabs.TabLayout

class HomeActivity : AppCompatActivity() {

    private lateinit var homeToolbar: Toolbar
    private lateinit var homeViewPager: ViewPager
    private lateinit var homeTabLayout: TabLayout
    private lateinit var homeTabAdapter: HomeTabAdapter

    // private var currentUser: FirebaseUser? = null
    // private lateinit var firebaseAuth: FirebaseAuth
    // TODO: private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        homeToolbar = findViewById(R.id.home_toolbar)
        setSupportActionBar(homeToolbar)

        homeViewPager = findViewById(R.id.home_tab_pager)
        homeTabAdapter =  HomeTabAdapter(supportFragmentManager)
        homeViewPager.adapter = homeTabAdapter

        homeTabLayout = findViewById(R.id.home_tabs)
        homeTabLayout.setupWithViewPager(homeViewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.home_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        if (item.itemId == R.id.home_menu_settings) {
            sendUserToSettingsActivity()
        }
        return true
    }

    private fun verifyUserExistence() {
        //val currentUserId = firebaseAuth.currentUser!!.uid
        // TODO: Connect to database
    }

    private fun sendUserToSettingsActivity() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }

    private fun sendUserToLoginActivity() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }
}
