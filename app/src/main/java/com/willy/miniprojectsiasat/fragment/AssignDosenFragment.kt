package com.willy.miniprojectsiasat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willy.miniprojectsiasat.adapter.MatakuliahAdapter
import com.willy.miniprojectsiasat.databinding.FragmentAssignDosenBinding
import com.willy.miniprojectsiasat.model.Constants
import com.willy.miniprojectsiasat.model.Matakuliah
import com.willy.miniprojectsiasat.model.User
import com.willy.miniprojectsiasat.repository.FirebaseRepository
import android.content.Context
import com.willy.miniprojectsiasat.activity.KaprogdiActivity

class AssignDosenFragment : Fragment() {
    private var _binding: FragmentAssignDosenBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var matakuliahAdapter: MatakuliahAdapter
    private var dataChangeListener: OnDataChangeListener? = null
    
    private val matakuliahList = mutableListOf<Matakuliah>()
    private val dosenList = mutableListOf<User>()
    
    // Interface untuk komunikasi dengan Activity
    interface OnDataChangeListener {
        fun onDosenAssigned()
    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDataChangeListener) {
            dataChangeListener = context
        }
    }
    
    override fun onDetach() {
        super.onDetach()
        dataChangeListener = null
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssignDosenBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Register fragment ke activity untuk bisa menerima refresh callback
        (activity as? KaprogdiActivity)?.setAssignDosenFragment(this)
        
        firebaseRepository = FirebaseRepository()
        setupRecyclerView()
        setupClickListeners()
        loadData()
    }
    
    private fun setupRecyclerView() {
        matakuliahAdapter = MatakuliahAdapter() // Jangan pass reference list asli
        binding.rvMatakuliahDosen.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matakuliahAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.btnAssignDosen.setOnClickListener {
            assignDosen()
        }
    }
    
    private fun loadData() {
        binding.btnAssignDosen.isEnabled = false
        
        // Load matakuliah first
        firebaseRepository.getAllMatakuliah { matakuliahListResult ->
            matakuliahList.clear()
            matakuliahList.addAll(matakuliahListResult)
            
            // Then load dosen
            firebaseRepository.getAllUsers { userList ->
                val allDosen = userList.filter { it.role == Constants.ROLE_DOSEN }
                dosenList.clear()
                dosenList.addAll(allDosen)
                
                // Setup spinners after both data loaded
                setupMatakuliahSpinner()
                setupDosenSpinner()
                matakuliahAdapter.updateData(matakuliahList, dosenList)
                
                binding.btnAssignDosen.isEnabled = true
            }
        }
    }
    
    private fun setupMatakuliahSpinner() {
        val matakuliahNames = mutableListOf("Pilih Matakuliah").apply {
            addAll(matakuliahList.map { "${it.id} - ${it.nama}" })
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            matakuliahNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMatakuliah.adapter = adapter
    }
    
    private fun setupDosenSpinner() {
        val dosenNames = mutableListOf("Pilih Dosen").apply {
            addAll(dosenList.map { "${it.id} - ${it.nama}" })
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            dosenNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDosen.adapter = adapter
    }
    
    private fun assignDosen() {
        val matakuliahPosition = binding.spinnerMatakuliah.selectedItemPosition
        val dosenPosition = binding.spinnerDosen.selectedItemPosition
        
        // Validasi pilihan matakuliah (position 0 adalah "Pilih Matakuliah")
        if (matakuliahPosition <= 0) {
            Toast.makeText(context, "Pilih matakuliah terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validasi pilihan dosen (position 0 adalah "Pilih Dosen")
        if (dosenPosition <= 0) {
            Toast.makeText(context, "Pilih dosen terlebih dahulu", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Ambil data yang dipilih
        val actualMatakuliahIndex = matakuliahPosition - 1
        val actualDosenIndex = dosenPosition - 1
        
        // Safety check untuk bounds
        if (actualMatakuliahIndex >= 0 && actualMatakuliahIndex < matakuliahList.size && 
            actualDosenIndex >= 0 && actualDosenIndex < dosenList.size) {
            
            val selectedMatakuliah = matakuliahList[actualMatakuliahIndex]
            val selectedDosen = dosenList[actualDosenIndex]
            
            binding.btnAssignDosen.isEnabled = false
            
            firebaseRepository.updateDosenPengampu(
                selectedMatakuliah.id,
                selectedDosen.id
            ) { success ->
                binding.btnAssignDosen.isEnabled = true
                
                if (success) {
                    Toast.makeText(context, "Dosen ${selectedDosen.nama} berhasil di-assign ke ${selectedMatakuliah.nama}", Toast.LENGTH_SHORT).show()
                    loadData() // Refresh data
                    dataChangeListener?.onDosenAssigned() // Notify activity
                } else {
                    Toast.makeText(context, "Gagal assign dosen", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Terjadi kesalahan pada data. Silakan refresh halaman.", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun refreshData() {
        if (_binding != null) {
            loadData()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 