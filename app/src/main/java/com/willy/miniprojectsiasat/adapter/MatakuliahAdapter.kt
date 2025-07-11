package com.willy.miniprojectsiasat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willy.miniprojectsiasat.databinding.ItemMatakuliahBinding
import com.willy.miniprojectsiasat.model.Matakuliah
import com.willy.miniprojectsiasat.model.User

class MatakuliahAdapter(
    private val matakuliahList: MutableList<Matakuliah> = mutableListOf(),
    private var dosenList: List<User> = emptyList(),
    private val onItemClick: ((Matakuliah) -> Unit)? = null
) : RecyclerView.Adapter<MatakuliahAdapter.MatakuliahViewHolder>() {

    fun updateData(newMatakuliahList: List<Matakuliah>, newDosenList: List<User>) {
        matakuliahList.clear()
        matakuliahList.addAll(newMatakuliahList.toList()) // Create copy to avoid reference issues
        dosenList = newDosenList.toList() // Update dosenList juga!
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatakuliahViewHolder {
        val binding = ItemMatakuliahBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatakuliahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatakuliahViewHolder, position: Int) {
        holder.bind(matakuliahList[position])
    }

    override fun getItemCount(): Int = matakuliahList.size

    inner class MatakuliahViewHolder(
        private val binding: ItemMatakuliahBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(matakuliah: Matakuliah) {
            binding.apply {
                tvKodeMK.text = matakuliah.id
                tvNamaMK.text = matakuliah.nama
                tvSKS.text = matakuliah.sks.toString()
                
                // Cari nama dosen berdasarkan ID
                val dosenName = if (matakuliah.dosen_pengampu != null) {
                    val foundDosen = dosenList.find { it.id == matakuliah.dosen_pengampu }
                    foundDosen?.nama ?: "Dosen tidak ditemukan"
                } else {
                    "Belum ada dosen"
                }
                tvDosenPengampu.text = dosenName
                println("FINAL DEBUG ${matakuliah.id}: tvDosenPengampu.text = '${tvDosenPengampu.text}'")
                
                // Format jadwal
                val jadwalText = "${matakuliah.jadwal.hari}, ${matakuliah.jadwal.jam_mulai}-${matakuliah.jadwal.jam_selesai}"
                tvJadwal.text = jadwalText
                tvRuangan.text = matakuliah.jadwal.ruangan
                
                // Set click listener
                root.setOnClickListener {
                    onItemClick?.invoke(matakuliah)
                }
            }
        }
    }
} 