package com.willy.miniprojectsiasat.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.willy.miniprojectsiasat.databinding.ActivityLoginBinding
import com.willy.miniprojectsiasat.model.Constants
import com.willy.miniprojectsiasat.repository.FirebaseRepository
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize dependencies
        firebaseRepository = FirebaseRepository()
        sharedPrefsHelper = SharedPrefsHelper(this)
        
        // Check if user is already logged in
        if (sharedPrefsHelper.isLoggedIn()) {
            navigateToMainActivity()
            return
        }
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val nimNip = binding.etNimNip.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (validateInput(nimNip, password)) {
                performLogin(nimNip, password)
            }
        }
    }
    
    private fun validateInput(nimNip: String, password: String): Boolean {
        if (nimNip.isEmpty()) {
            binding.etNimNip.error = "NIM/NIP tidak boleh kosong"
            return false
        }
        
        if (password.isEmpty()) {
            binding.etPassword.error = "Password tidak boleh kosong"
            return false
        }
        
        return true
    }
    
    private fun performLogin(nimNip: String, password: String) {
        showLoading(true)
        
        firebaseRepository.authenticateUser(nimNip, password) { user ->
            showLoading(false)
            
            if (user != null) {
                // Save user session
                sharedPrefsHelper.saveUserSession(user.id, user.nama, user.role)
                
                // Navigate based on role
                when (user.role) {
                    Constants.ROLE_KAPROGDI -> {
                        startActivity(Intent(this, KaprogdiActivity::class.java))
                    }
                    Constants.ROLE_DOSEN -> {
                        startActivity(Intent(this, DosenActivity::class.java))
                    }
                    Constants.ROLE_MAHASISWA -> {
                        startActivity(Intent(this, MahasiswaActivity::class.java))
                    }
                }
                finish()
            } else {
                Toast.makeText(this, "NIM/NIP atau password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
        binding.etNimNip.isEnabled = !isLoading
        binding.etPassword.isEnabled = !isLoading
    }
    
    private fun navigateToMainActivity() {
        val userRole = sharedPrefsHelper.getUserRole()
        when (userRole) {
            Constants.ROLE_KAPROGDI -> {
                startActivity(Intent(this, KaprogdiActivity::class.java))
            }
            Constants.ROLE_DOSEN -> {
                startActivity(Intent(this, DosenActivity::class.java))
            }
            Constants.ROLE_MAHASISWA -> {
                startActivity(Intent(this, MahasiswaActivity::class.java))
            }
        }
        finish()
    }
} 