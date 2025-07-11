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

class NilaiMahasiswaFragment : Fragment() {
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
        loadNilaiMahasiswa()
    }
    
    private fun setupRecyclerView() {
        // showNilai = true karena ini untuk menampilkan nilai
        krsAdapter = KRSAdapter(showNilai = true)
        binding.rvData.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = krsAdapter
        }
    }
    
    private fun loadNilaiMahasiswa() {
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
            
            // Load semua matakuliah untuk mendapatkan detail
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
                    
                    // Sort berdasarkan kode matakuliah
                    val sortedKrsList = krsList.sortedBy { it.matakuliah.id }
                    
                    krsAdapter.updateData(sortedKrsList)
                    updateEmptyView(sortedKrsList.isEmpty(), "Tidak ada data nilai tersedia")
                }
            }
        }
    }
    
    private fun updateEmptyView(isEmpty: Boolean, message: String = "Tidak ada nilai") {
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