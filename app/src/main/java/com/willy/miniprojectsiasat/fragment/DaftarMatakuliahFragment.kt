package com.willy.miniprojectsiasat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willy.miniprojectsiasat.adapter.MatakuliahRegisterAdapter
import com.willy.miniprojectsiasat.adapter.MatakuliahRegisterItem
import com.willy.miniprojectsiasat.databinding.FragmentDataListBinding
import com.willy.miniprojectsiasat.model.Constants
import com.willy.miniprojectsiasat.model.Matakuliah
import com.willy.miniprojectsiasat.repository.FirebaseRepository
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class DaftarMatakuliahFragment : Fragment() {
    private var _binding: FragmentDataListBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var matakuliahRegisterAdapter: MatakuliahRegisterAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        firebaseRepository = FirebaseRepository()
        sharedPrefsHelper = SharedPrefsHelper(requireContext())
        setupRecyclerView()
        loadMatakuliah()
    }
    
    private fun setupRecyclerView() {
        matakuliahRegisterAdapter = MatakuliahRegisterAdapter { matakuliah ->
            registerMatakuliah(matakuliah)
        }
        binding.rvData.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matakuliahRegisterAdapter
        }
    }
    
    private fun loadMatakuliah() {
        val mahasiswaId = sharedPrefsHelper.getUserId()
        
        if (mahasiswaId == null) {
            updateEmptyView(true, "User tidak ditemukan")
            return
        }
        
        // Load semua matakuliah
        firebaseRepository.getAllMatakuliah { allMatakuliah ->
            if (allMatakuliah.isEmpty()) {
                updateEmptyView(true, "Tidak ada matakuliah tersedia")
                return@getAllMatakuliah
            }
            
            // Load KRS mahasiswa untuk cek status registrasi
            firebaseRepository.getKRSMahasiswa(mahasiswaId) { krsMap ->
                // Load data dosen
                firebaseRepository.getAllUsers { userList ->
                    val dosenList = userList.filter { it.role == Constants.ROLE_DOSEN }
                    
                    val matakuliahRegisterList = allMatakuliah.map { matakuliah ->
                        val dosenName = if (matakuliah.dosen_pengampu != null) {
                            dosenList.find { it.id == matakuliah.dosen_pengampu }?.nama ?: "Dosen tidak ditemukan"
                        } else {
                            "Belum ada dosen"
                        }
                        
                        val isRegistered = krsMap.containsKey(matakuliah.id)
                        
                        MatakuliahRegisterItem(matakuliah, dosenName, isRegistered)
                    }
                    
                    matakuliahRegisterAdapter.updateData(matakuliahRegisterList)
                    updateEmptyView(false)
                }
            }
        }
    }
    
    private fun registerMatakuliah(matakuliah: Matakuliah) {
        val mahasiswaId = sharedPrefsHelper.getUserId()
        
        if (mahasiswaId == null) {
            Toast.makeText(context, "User tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }
        
        firebaseRepository.registerMatakuliah(mahasiswaId, matakuliah.id) { success ->
            if (success) {
                Toast.makeText(context, "Berhasil mendaftar matakuliah ${matakuliah.nama}", Toast.LENGTH_SHORT).show()
                loadMatakuliah() // Refresh data
            } else {
                Toast.makeText(context, "Gagal mendaftar matakuliah", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateEmptyView(isEmpty: Boolean, message: String = "Tidak ada matakuliah tersedia") {
        if (isEmpty) {
            binding.rvData.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = message
        } else {
            binding.rvData.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 