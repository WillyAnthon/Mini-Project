package com.willy.miniprojectsiasat.model

data class Jadwal(
    val hari: String = "",
    val jam_mulai: String = "",
    val jam_selesai: String = "",
    val ruangan: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", "", "")
} 