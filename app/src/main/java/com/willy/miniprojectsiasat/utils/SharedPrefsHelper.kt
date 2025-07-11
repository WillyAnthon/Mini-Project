package com.willy.miniprojectsiasat.utils

import android.content.Context
import android.content.SharedPreferences
import com.willy.miniprojectsiasat.model.Constants

class SharedPrefsHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)
    
    fun saveUserSession(userId: String, userName: String, userRole: String) {
        prefs.edit().apply {
            putString(Constants.PREF_USER_ID, userId)
            putString(Constants.PREF_USER_NAME, userName)
            putString(Constants.PREF_USER_ROLE, userRole)
            apply()
        }
    }
    
    fun getUserId(): String? {
        return prefs.getString(Constants.PREF_USER_ID, null)
    }
    
    fun getUserName(): String? {
        return prefs.getString(Constants.PREF_USER_NAME, null)
    }
    
    fun getUserRole(): String? {
        return prefs.getString(Constants.PREF_USER_ROLE, null)
    }
    
    fun isLoggedIn(): Boolean {
        return getUserId() != null
    }
    
    fun clearSession() {
        prefs.edit().clear().apply()
    }
} 