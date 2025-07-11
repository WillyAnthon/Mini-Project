package com.willy.miniprojectsiasat.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.willy.miniprojectsiasat.databinding.FragmentTambahMatakuliahBinding
import com.willy.miniprojectsiasat.model.Jadwal
import com.willy.miniprojectsiasat.model.Matakuliah
import com.willy.miniprojectsiasat.repository.FirebaseRepository

class TambahMatakuliahFragment : Fragment() {
    private var _binding: FragmentTambahMatakuliahBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRepository: FirebaseRepository
    private var dataChangeListener: OnDataChangeListener? = null
    
    // Interface untuk komunikasi dengan Activity
    interface OnDataChangeListener {
        fun onMatakuliahAdded()
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
        _binding = FragmentTambahMatakuliahBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        firebaseRepository = FirebaseRepository()
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnTambahMK.setOnClickListener {
            if (validateInput()) {
                addMatakuliah()
            }
        }
    }
    
    private fun validateInput(): Boolean {
        val kodeMK = binding.etKodeMK.text.toString().trim()
        val namaMK = binding.etNamaMK.text.toString().trim()
        val sks = binding.etSKS.text.toString().trim()
        val hari = binding.etHari.text.toString().trim()
        val jamMulai = binding.etJamMulai.text.toString().trim()
        val jamSelesai = binding.etJamSelesai.text.toString().trim()
        val ruangan = binding.etRuangan.text.toString().trim()
        
        when {
            kodeMK.isEmpty() -> {
                binding.etKodeMK.error = "Kode matakuliah tidak boleh kosong"
                return false
            }
            namaMK.isEmpty() -> {
                binding.etNamaMK.error = "Nama matakuliah tidak boleh kosong"
                return false
            }
            sks.isEmpty() -> {
                binding.etSKS.error = "SKS tidak boleh kosong"
                return false
            }
            hari.isEmpty() -> {
                binding.etHari.error = "Hari tidak boleh kosong"
                return false
            }
            jamMulai.isEmpty() -> {
                binding.etJamMulai.error = "Jam mulai tidak boleh kosong"
                return false
            }
            jamSelesai.isEmpty() -> {
                binding.etJamSelesai.error = "Jam selesai tidak boleh kosong"
                return false
            }
            ruangan.isEmpty() -> {
                binding.etRuangan.error = "Ruangan tidak boleh kosong"
                return false
            }
        }
        
        return true
    }
    
    private fun addMatakuliah() {
        val kodeMK = binding.etKodeMK.text.toString().trim()
        val namaMK = binding.etNamaMK.text.toString().trim()
        val sks = binding.etSKS.text.toString().trim().toIntOrNull() ?: 0
        val hari = binding.etHari.text.toString().trim()
        val jamMulai = binding.etJamMulai.text.toString().trim()
        val jamSelesai = binding.etJamSelesai.text.toString().trim()
        val ruangan = binding.etRuangan.text.toString().trim()
        
        val jadwal = Jadwal(hari, jamMulai, jamSelesai, ruangan)
        val matakuliah = Matakuliah(kodeMK, namaMK, sks, null, jadwal)
        
        binding.btnTambahMK.isEnabled = false
        
        firebaseRepository.addMatakuliah(matakuliah) { success ->
            binding.btnTambahMK.isEnabled = true
            
            if (success) {
                Toast.makeText(context, "Matakuliah berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                clearForm()
                // Notify activity bahwa data sudah berubah
                dataChangeListener?.onMatakuliahAdded()
            } else {
                Toast.makeText(context, "Gagal menambahkan matakuliah", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun clearForm() {
        binding.etKodeMK.text?.clear()
        binding.etNamaMK.text?.clear()
        binding.etSKS.text?.clear()
        binding.etHari.text?.clear()
        binding.etJamMulai.text?.clear()
        binding.etJamSelesai.text?.clear()
        binding.etRuangan.text?.clear()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 