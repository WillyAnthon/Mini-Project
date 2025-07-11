package com.willy.miniprojectsiasat.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.willy.miniprojectsiasat.R
import com.willy.miniprojectsiasat.databinding.ActivityDosenBinding
import com.willy.miniprojectsiasat.fragment.MatakuliahDosenFragment
import com.willy.miniprojectsiasat.fragment.InputNilaiFragment
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class DosenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDosenBinding
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPrefsHelper = SharedPrefsHelper(this)
        
        setupActionBar()
        setupViewPager()
    }
    
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Dashboard Dosen"
    }
    
    private fun setupViewPager() {
        val adapter = DosenPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Matakuliah Saya"
                1 -> "Input Nilai"
                else -> ""
            }
        }.attach()
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        sharedPrefsHelper.clearSession()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    
    private class DosenPagerAdapter(fragmentActivity: FragmentActivity) : 
        FragmentStateAdapter(fragmentActivity) {
        
        override fun getItemCount(): Int = 2
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> MatakuliahDosenFragment()
                1 -> InputNilaiFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }
} 