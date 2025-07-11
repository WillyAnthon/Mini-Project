package com.willy.miniprojectsiasat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willy.miniprojectsiasat.adapter.MatakuliahAdapter
import com.willy.miniprojectsiasat.adapter.UserAdapter
import com.willy.miniprojectsiasat.databinding.FragmentDataListBinding
import com.willy.miniprojectsiasat.model.Constants
import com.willy.miniprojectsiasat.repository.FirebaseRepository

class DataListFragment : Fragment() {
    private var _binding: FragmentDataListBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRepository: FirebaseRepository
    
    private var dataType: String = ""
    
    companion object {
        private const val ARG_DATA_TYPE = "data_type"
        
        fun newInstance(dataType: String): DataListFragment {
            val fragment = DataListFragment()
            val args = Bundle()
            args.putString(ARG_DATA_TYPE, dataType)
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataType = arguments?.getString(ARG_DATA_TYPE) ?: ""
    }
    
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
        setupRecyclerView()
        loadData()
    }
    
    // Method untuk refresh data dari luar
    fun refreshData() {
        if (_binding != null) {
            loadData()
        }
    }
    
    private fun setupRecyclerView() {
        binding.rvData.layoutManager = LinearLayoutManager(context)
    }
    
    private fun loadData() {
        when (dataType) {
            "matakuliah" -> loadMatakuliahData()
            "dosen" -> loadDosenData()
            "mahasiswa" -> loadMahasiswaData()
        }
    }
    
    private fun loadMatakuliahData() {
        // Load matakuliah dan dosen untuk menampilkan nama dosen
        firebaseRepository.getAllMatakuliah { matakuliahList ->
            firebaseRepository.getAllUsers { userList ->
                val dosenList = userList.filter { it.role == Constants.ROLE_DOSEN }
                
                val adapter = MatakuliahAdapter()
                adapter.updateData(matakuliahList.toMutableList(), dosenList)
                binding.rvData.adapter = adapter
                
                updateEmptyView(matakuliahList.isEmpty())
            }
        }
    }
    
    private fun loadDosenData() {
        firebaseRepository.getAllUsers { userList ->
            val dosenList = userList.filter { it.role == Constants.ROLE_DOSEN }
            
            val adapter = UserAdapter(dosenList.toMutableList())
            binding.rvData.adapter = adapter
            
            updateEmptyView(dosenList.isEmpty())
        }
    }
    
    private fun loadMahasiswaData() {
        firebaseRepository.getAllUsers { userList ->
            val mahasiswaList = userList.filter { it.role == Constants.ROLE_MAHASISWA }
            
            val adapter = UserAdapter(mahasiswaList.toMutableList())
            binding.rvData.adapter = adapter
            
            updateEmptyView(mahasiswaList.isEmpty())
        }
    }
    
    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvData.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = when (dataType) {
                "matakuliah" -> "Tidak ada matakuliah"
                "dosen" -> "Tidak ada data dosen"
                "mahasiswa" -> "Tidak ada data mahasiswa"
                else -> "Tidak ada data"
            }
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