package com.willy.miniprojectsiasat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.willy.miniprojectsiasat.activity.LoginActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Langsung redirect ke LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}