package com.willy.miniprojectsiasat.repository

import com.google.firebase.database.*
import com.willy.miniprojectsiasat.model.*

class FirebaseRepository {
    private val database = FirebaseDatabase.getInstance()
    
    // Login - verify user credentials
    fun authenticateUser(id: String, password: String, callback: (User?) -> Unit) {
        database.getReference(Constants.NODE_USERS)
            .child(id)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        if (user != null && user.password == password) {
                            callback(user.copy(id = id))
                        } else {
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }
    
    // Get all users
    fun getAllUsers(callback: (List<User>) -> Unit) {
        database.getReference(Constants.NODE_USERS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
                        if (user != null) {
                            users.add(user.copy(id = userSnapshot.key ?: ""))
                        }
                    }
                    callback(users)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
    
    // Get all matakuliah
    fun getAllMatakuliah(callback: (List<Matakuliah>) -> Unit) {
        database.getReference(Constants.NODE_MATAKULIAH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val matakuliahList = mutableListOf<Matakuliah>()
                    for (mkSnapshot in snapshot.children) {
                        val matakuliah = mkSnapshot.getValue(Matakuliah::class.java)
                        if (matakuliah != null) {
                            matakuliahList.add(matakuliah.copy(id = mkSnapshot.key ?: ""))
                        }
                    }
                    callback(matakuliahList)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
    
    // Add new matakuliah (Kaprogdi)
    fun addMatakuliah(matakuliah: Matakuliah, callback: (Boolean) -> Unit) {
        val ref = database.getReference(Constants.NODE_MATAKULIAH).child(matakuliah.id)
        ref.setValue(matakuliah.copy(id = ""))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
    
    // Update dosen pengampu (Kaprogdi)
    fun updateDosenPengampu(matakuliahId: String, dosenId: String?, callback: (Boolean) -> Unit) {
        val ref = database.getReference(Constants.NODE_MATAKULIAH)
            .child(matakuliahId)
            .child("dosen_pengampu")
        ref.setValue(dosenId)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
    
    // Get matakuliah by dosen (Dosen)
    fun getMatakuliahByDosen(dosenId: String, callback: (List<Matakuliah>) -> Unit) {
        database.getReference(Constants.NODE_MATAKULIAH)
            .orderByChild("dosen_pengampu")
            .equalTo(dosenId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val matakuliahList = mutableListOf<Matakuliah>()
                    for (mkSnapshot in snapshot.children) {
                        val matakuliah = mkSnapshot.getValue(Matakuliah::class.java)
                        if (matakuliah != null) {
                            matakuliahList.add(matakuliah.copy(id = mkSnapshot.key ?: ""))
                        }
                    }
                    callback(matakuliahList)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
    
    // Get mahasiswa in specific matakuliah
    fun getMahasiswaInMatakuliah(matakuliahId: String, callback: (List<Pair<String, String>>) -> Unit) {
        database.getReference(Constants.NODE_KRS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mahasiswaList = mutableListOf<Pair<String, String>>()
                    for (mahasiswaSnapshot in snapshot.children) {
                        val mahasiswaId = mahasiswaSnapshot.key ?: ""
                        if (mahasiswaSnapshot.hasChild(matakuliahId)) {
                            val krsData = mahasiswaSnapshot.child(matakuliahId).getValue(KRS::class.java)
                            mahasiswaList.add(Pair(mahasiswaId, krsData?.nilai ?: ""))
                        }
                    }
                    callback(mahasiswaList)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }
    
    // Update nilai mahasiswa (Dosen)
    fun updateNilaiMahasiswa(mahasiswaId: String, matakuliahId: String, nilai: String, callback: (Boolean) -> Unit) {
        val ref = database.getReference(Constants.NODE_KRS)
            .child(mahasiswaId)
            .child(matakuliahId)
            .child("nilai")
        ref.setValue(nilai)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
    
    // Register matakuliah (Mahasiswa)
    fun registerMatakuliah(mahasiswaId: String, matakuliahId: String, callback: (Boolean) -> Unit) {
        val ref = database.getReference(Constants.NODE_KRS)
            .child(mahasiswaId)
            .child(matakuliahId)
        ref.setValue(KRS(""))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
    
    // Get KRS mahasiswa
    fun getKRSMahasiswa(mahasiswaId: String, callback: (Map<String, KRS>) -> Unit) {
        database.getReference(Constants.NODE_KRS)
            .child(mahasiswaId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val krsMap = mutableMapOf<String, KRS>()
                    for (mkSnapshot in snapshot.children) {
                        val matakuliahId = mkSnapshot.key ?: ""
                        val krs = mkSnapshot.getValue(KRS::class.java)
                        if (krs != null) {
                            krsMap[matakuliahId] = krs
                        }
                    }
                    callback(krsMap)
                }
                
                override fun onCancelled(error: DatabaseError) {
                    callback(emptyMap())
                }
            })
    }
} 