package com.willy.miniprojectsiasat.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.willy.miniprojectsiasat.adapter.MatakuliahAdapter
import com.willy.miniprojectsiasat.databinding.FragmentDataListBinding
import com.willy.miniprojectsiasat.model.Constants
import com.willy.miniprojectsiasat.repository.FirebaseRepository
import com.willy.miniprojectsiasat.utils.SharedPrefsHelper

class MatakuliahDosenFragment : Fragment() {
    private var _binding: FragmentDataListBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var sharedPrefsHelper: SharedPrefsHelper
    private lateinit var matakuliahAdapter: MatakuliahAdapter
    
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
        matakuliahAdapter = MatakuliahAdapter()
        binding.rvData.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matakuliahAdapter
        }
    }
    
    private fun loadMatakuliah() {
        val dosenId = sharedPrefsHelper.getUserId()
        
        if (dosenId != null) {
            firebaseRepository.getMatakuliahByDosen(dosenId) { matakuliahList ->
                // Load dosen data untuk ditampilkan di adapter
                firebaseRepository.getAllUsers { userList ->
                    val dosenList = userList.filter { it.role == Constants.ROLE_DOSEN }
                    matakuliahAdapter.updateData(matakuliahList, dosenList)
                    
                    updateEmptyView(matakuliahList.isEmpty())
                }
            }
        } else {
            updateEmptyView(true)
        }
    }
    
    private fun updateEmptyView(isEmpty: Boolean) {
        if (isEmpty) {
            binding.rvData.visibility = View.GONE
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "Anda belum mengampu matakuliah apapun"
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