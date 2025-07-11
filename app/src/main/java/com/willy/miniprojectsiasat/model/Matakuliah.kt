package com.willy.miniprojectsiasat.model

data class Matakuliah(
    val id: String = "",
    val nama: String = "",
    val sks: Int = 0,
    val dosen_pengampu: String? = null,
    val jadwal: Jadwal = Jadwal()
) {
    // No-argument constructor for Firebase
    constructor() : this("", "", 0, null, Jadwal())
} 