package com.willy.miniprojectsiasat.model

data class User(
    val id: String = "",
    val nama: String = "",
    val role: String = "",
    val password: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "")
} 