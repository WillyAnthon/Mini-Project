package com.willy.miniprojectsiasat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willy.miniprojectsiasat.adapter.KRSAdapter
import com.willy.miniprojectsiasat.adapter.KRSItem
import com.willy.miniprojectsiasat.databinding.FragmentDataListBinding
import com.willy.miniprojectsiasat.model.Constants
import com.willy.miniprojectsiasat.repository.FirebaseRepository
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class JadwalMahasiswaFragment : Fragment() {
    private var _binding: FragmentDataListBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var krsAdapter: KRSAdapter
    
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
        loadJadwalMahasiswa()
    }
    
    private fun setupRecyclerView() {
        // showNilai = false karena ini untuk jadwal, bukan nilai
        krsAdapter = KRSAdapter(showNilai = false)
        binding.rvData.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = krsAdapter
        }
    }
    
    private fun loadJadwalMahasiswa() {
        val mahasiswaId = sharedPrefsHelper.getUserId()
        
        if (mahasiswaId == null) {
            updateEmptyView(true, "User tidak ditemukan")
            return
        }
        
        // Load KRS mahasiswa
        firebaseRepository.getKRSMahasiswa(mahasiswaId) { krsMap ->
            if (krsMap.isEmpty()) {
                updateEmptyView(true, "Anda belum mengambil matakuliah apapun")
                return@getKRSMahasiswa
            }
            
            // Load semua matakuliah untuk mendapatkan detail jadwal
            firebaseRepository.getAllMatakuliah { allMatakuliah ->
                // Load data dosen
                firebaseRepository.getAllUsers { userList ->
                    val dosenList = userList.filter { it.role == Constants.ROLE_DOSEN }
                    
                    val krsList = mutableListOf<KRSItem>()
                    
                    for ((matakuliahId, krs) in krsMap) {
                        val matakuliah = allMatakuliah.find { it.id == matakuliahId }
                        if (matakuliah != null) {
                            val dosenName = if (matakuliah.dosen_pengampu != null) {
                                dosenList.find { it.id == matakuliah.dosen_pengampu }?.nama 
                                    ?: "Dosen tidak ditemukan"
                            } else {
                                "Belum ada dosen"
                            }
                            
                            krsList.add(KRSItem(matakuliah, krs, dosenName))
                        }
                    }
                    
                    // Sort berdasarkan hari dan jam
                    val sortedKrsList = krsList.sortedWith(compareBy<KRSItem> { 
                        getDayOrder(it.matakuliah.jadwal.hari)
                    }.thenBy { 
                        it.matakuliah.jadwal.jam_mulai 
                    })
                    
                    krsAdapter.updateData(sortedKrsList)
                    updateEmptyView(sortedKrsList.isEmpty(), "Tidak ada jadwal tersedia")
                }
            }
        }
    }
    
    private fun getDayOrder(hari: String): Int {
        return when (hari.lowercase()) {
            "senin" -> 1
            "selasa" -> 2
            "rabu" -> 3
            "kamis" -> 4
            "jumat" -> 5
            "sabtu" -> 6
            "minggu" -> 7
            else -> 8
        }
    }
    
    private fun updateEmptyView(isEmpty: Boolean, message: String = "Tidak ada jadwal") {
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