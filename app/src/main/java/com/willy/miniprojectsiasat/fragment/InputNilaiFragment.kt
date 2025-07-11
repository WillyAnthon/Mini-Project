package com.willy.miniprojectsiasat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willy.miniprojectsiasat.adapter.MahasiswaNilaiAdapter
import com.willy.miniprojectsiasat.adapter.MahasiswaNilaiItem
import com.willy.miniprojectsiasat.databinding.FragmentInputNilaiBinding
import com.willy.miniprojectsiasat.model.Constants
import com.willy.miniprojectsiasat.model.Matakuliah
import com.willy.miniprojectsiasat.model.User
import com.willy.miniprojectsiasat.repository.FirebaseRepository
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class InputNilaiFragment : Fragment() {
    private var _binding: FragmentInputNilaiBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var mahasiswaNilaiAdapter: MahasiswaNilaiAdapter
    
    private var matakuliahList = mutableListOf<Matakuliah>()
    private var selectedMatakuliah: Matakuliah? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputNilaiBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        firebaseRepository = FirebaseRepository()
        sharedPrefsHelper = SharedPrefsHelper(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadMatakuliah()
    }
    
    private fun setupRecyclerView() {
        mahasiswaNilaiAdapter = MahasiswaNilaiAdapter { mahasiswaId, nilai ->
            simpanNilai(mahasiswaId, nilai)
        }
        binding.rvMahasiswa.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mahasiswaNilaiAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnLoadMahasiswa.setOnClickListener {
            loadMahasiswaInMatakuliah()
        }
    }
    
    private fun loadMatakuliah() {
        val dosenId = sharedPrefsHelper.getUserId()
        
        if (dosenId != null) {
            firebaseRepository.getMatakuliahByDosen(dosenId) { matakuliahListResult ->
                matakuliahList.clear()
                matakuliahList.addAll(matakuliahListResult)
                setupMatakuliahSpinner()
            }
        }
    }
    
    private fun setupMatakuliahSpinner() {
        if (matakuliahList.isEmpty()) {
            binding.tvEmpty.text = "Anda belum mengampu matakuliah apapun"
            return
        }
        
        val matakuliahNames = matakuliahList.map { "${it.id} - ${it.nama}" }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            matakuliahNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMatakuliah.adapter = adapter
    }
    
    private fun loadMahasiswaInMatakuliah() {
        val matakuliahPosition = binding.spinnerMatakuliah.selectedItemPosition
        
        if (matakuliahPosition < 0 || matakuliahPosition >= matakuliahList.size) {
            Toast.makeText(context, "Pilih matakuliah terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        
        selectedMatakuliah = matakuliahList[matakuliahPosition]
        
        // Load mahasiswa yang mengambil matakuliah ini
        firebaseRepository.getMahasiswaInMatakuliah(selectedMatakuliah!!.id) { mahasiswaDataList ->
            if (mahasiswaDataList.isEmpty()) {
                updateEmptyView(true, "Tidak ada mahasiswa yang mengambil matakuliah ini")
                return@getMahasiswaInMatakuliah
            }
            
            // Load data lengkap mahasiswa
            firebaseRepository.getAllUsers { userList ->
                val mahasiswaNilaiList = mutableListOf<MahasiswaNilaiItem>()
                
                for ((mahasiswaId, currentNilai) in mahasiswaDataList) {
                    val mahasiswa = userList.find { it.id == mahasiswaId && it.role == Constants.ROLE_MAHASISWA }
                    if (mahasiswa != null) {
                        mahasiswaNilaiList.add(MahasiswaNilaiItem(mahasiswa, currentNilai))
                    }
                }
                
                mahasiswaNilaiAdapter.updateData(mahasiswaNilaiList)
                updateEmptyView(mahasiswaNilaiList.isEmpty(), "Tidak ada data mahasiswa yang valid")
            }
        }
    }
    
    private fun simpanNilai(mahasiswaId: String, nilai: String) {
        if (selectedMatakuliah == null) {
            Toast.makeText(context, "Pilih matakuliah terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        
        firebaseRepository.updateNilaiMahasiswa(mahasiswaId, selectedMatakuliah!!.id, nilai) { success ->
            if (success) {
                Toast.makeText(context, "Nilai berhasil disimpan", Toast.LENGTH_SHORT).show()
                loadMahasiswaInMatakuliah() // Refresh data
            } else {
                Toast.makeText(context, "Gagal menyimpan nilai", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateEmptyView(isEmpty: Boolean, message: String = "") {
        if (isEmpty) {
            binding.rvMahasiswa.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
            if (message.isNotEmpty()) {
                binding.tvEmpty.text = message
            }
        } else {
            binding.rvMahasiswa.visibility = View.VISIBLE
            binding.tvEmpty.visibility = View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 