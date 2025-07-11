package com.willy.miniprojectsiasat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.willy.miniprojectsiasat.databinding.ItemMahasiswaNilaiBinding
import com.willy.miniprojectsiasat.model.User

data class MahasiswaNilaiItem(
    val mahasiswa: User,
    val currentNilai: String = ""
)

class MahasiswaNilaiAdapter(
    private val mahasiswaList: MutableList<MahasiswaNilaiItem> = mutableListOf(),
    private val onSimpanNilai: ((String, String) -> Unit)? = null // mahasiswaId, nilai
) : RecyclerView.Adapter<MahasiswaNilaiAdapter.MahasiswaNilaiViewHolder>() {

    fun updateData(newMahasiswaList: List<MahasiswaNilaiItem>) {
        mahasiswaList.clear()
        mahasiswaList.addAll(newMahasiswaList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaNilaiViewHolder {
        val binding = ItemMahasiswaNilaiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MahasiswaNilaiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MahasiswaNilaiViewHolder, position: Int) {
        holder.bind(mahasiswaList[position])
    }

    override fun getItemCount(): Int = mahasiswaList.size

    inner class MahasiswaNilaiViewHolder(
        private val binding: ItemMahasiswaNilaiBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MahasiswaNilaiItem) {
            binding.apply {
                tvMahasiswaId.text = item.mahasiswa.id
                tvMahasiswaName.text = item.mahasiswa.nama
                
                // Tampilkan nilai saat ini jika ada
                if (item.currentNilai.isNotEmpty()) {
                    tvCurrentNilai.text = item.currentNilai
                    tvCurrentNilai.visibility = View.VISIBLE
                    etNilai.setText(item.currentNilai)
                } else {
                    tvCurrentNilai.visibility = View.GONE
                    etNilai.setText("")
                }
                
                btnSimpanNilai.setOnClickListener {
                    val nilai = etNilai.text.toString().trim().uppercase()
                    
                    if (nilai.isEmpty()) {
                        Toast.makeText(root.context, "Masukkan nilai terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    
                    if (!isValidNilai(nilai)) {
                        Toast.makeText(root.context, "Nilai harus A, B, C, D, atau E", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    
                    onSimpanNilai?.invoke(item.mahasiswa.id, nilai)
                }
            }
        }
        
        private fun isValidNilai(nilai: String): Boolean {
            return nilai in listOf("A", "B", "C", "D", "E")
        }
    }
} 