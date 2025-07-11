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
import com.willy.miniprojectsiasat.databinding.ActivityKaprogdiBinding
import com.willy.miniprojectsiasat.fragment.AssignDosenFragment
import com.willy.miniprojectsiasat.fragment.LihatDataFragment
import com.willy.miniprojectsiasat.fragment.TambahMatakuliahFragment
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class KaprogdiActivity : AppCompatActivity(), TambahMatakuliahFragment.OnDataChangeListener, 
    AssignDosenFragment.OnDataChangeListener {
    
    private lateinit var binding: ActivityKaprogdiBinding
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private var lihatDataFragment: LihatDataFragment? = null
    private var assignDosenFragment: AssignDosenFragment? = null
    
    // Interface untuk komunikasi dengan fragments
    interface DataRefreshListener {
        fun refreshData()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprogdiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        sharedPrefsHelper = SharedPrefsHelper(this)
        
        setupActionBar()
        setupViewPager()
    }
    
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Dashboard Kaprogdi"
    }
    
    private fun setupViewPager() {
        val adapter = KaprogdiPagerAdapter(this)
        binding.viewPager.adapter = adapter
        
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Tambah MK"
                1 -> "Assign Dosen"
                2 -> "Lihat Data"
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
    
    // Implementation dari OnDataChangeListener untuk refresh data
    override fun onMatakuliahAdded() {
        // Refresh data di LihatDataFragment dan AssignDosenFragment
        lihatDataFragment?.refreshData()
        assignDosenFragment?.refreshData()
    }
    
    override fun onDosenAssigned() {
        // Refresh data di LihatDataFragment
        lihatDataFragment?.refreshData()
    }
    
    fun setLihatDataFragment(fragment: LihatDataFragment) {
        this.lihatDataFragment = fragment
    }
    
    fun setAssignDosenFragment(fragment: AssignDosenFragment) {
        this.assignDosenFragment = fragment
    }
    
    private class KaprogdiPagerAdapter(fragmentActivity: FragmentActivity) : 
        FragmentStateAdapter(fragmentActivity) {
        
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> TambahMatakuliahFragment()
                1 -> AssignDosenFragment()
                2 -> LihatDataFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }
} 