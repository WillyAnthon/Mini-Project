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
import com.willy.miniprojectsiasat.databinding.ActivityMahasiswaBinding
import com.willy.miniprojectsiasat.fragment.DaftarMatakuliahFragment
import com.willy.miniprojectsiasat.fragment.JadwalMahasiswaFragment
import com.willy.miniprojectsiasat.fragment.NilaiMahasiswaFragment
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class MahasiswaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMahasiswaBinding
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMahasiswaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPrefsHelper = SharedPrefsHelper(this)
        
        setupActionBar()
        setupViewPager()
    }
    
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Dashboard Mahasiswa"
    }
    
    private fun setupViewPager() {
        val adapter = MahasiswaPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Daftar MK"
                1 -> "Jadwal Saya"
                2 -> "Nilai Saya"
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
    
    private class MahasiswaPagerAdapter(fragmentActivity: FragmentActivity) : 
        FragmentStateAdapter(fragmentActivity) {
        
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DaftarMatakuliahFragment()
                1 -> JadwalMahasiswaFragment()
                2 -> NilaiMahasiswaFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }
} 