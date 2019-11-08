package com.cs220.ssmessaging.frontend.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}
