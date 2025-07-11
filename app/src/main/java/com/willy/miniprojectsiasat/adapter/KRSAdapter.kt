package com.willy.miniprojectsiasat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willy.miniprojectsiasat.databinding.ItemKrsBinding
import com.willy.miniprojectsiasat.model.KRS
import com.willy.miniprojectsiasat.model.Matakuliah
import com.willy.miniprojectsiasat.model.User

data class KRSItem(
    val matakuliah: Matakuliah,
    val krs: KRS,
    val dosenName: String = ""
)

class KRSAdapter(
    private val krsList: MutableList<KRSItem> = mutableListOf(),
    private val showNilai: Boolean = true,
    private val onItemClick: ((KRSItem) -> Unit)? = null
) : RecyclerView.Adapter<KRSAdapter.KRSViewHolder>() {

    fun updateData(newKrsList: List<KRSItem>) {
        krsList.clear()
        krsList.addAll(newKrsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KRSViewHolder {
        val binding = ItemKrsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return KRSViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KRSViewHolder, position: Int) {
        holder.bind(krsList[position])
    }

    override fun getItemCount(): Int = krsList.size

    inner class KRSViewHolder(
        private val binding: ItemKrsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(krsItem: KRSItem) {
            binding.apply {
                val matakuliah = krsItem.matakuliah
                val krs = krsItem.krs
                
                tvKodeMK.text = matakuliah.id
                tvNamaMK.text = matakuliah.nama
                tvSKS.text = matakuliah.sks.toString()
                tvDosenPengampu.text = krsItem.dosenName.ifEmpty { "Belum ada dosen" }
                
                // Format jadwal
                val jadwalText = "${matakuliah.jadwal.hari}, ${matakuliah.jadwal.jam_mulai}-${matakuliah.jadwal.jam_selesai} (${matakuliah.jadwal.ruangan})"
                tvJadwal.text = jadwalText
                
                // Tampilkan nilai
                if (showNilai) {
                    if (krs.nilai.isNotEmpty()) {
                        tvNilai.text = krs.nilai
                        tvNilai.visibility = View.VISIBLE
                    } else {
                        tvNilai.text = "Belum ada nilai"
                        tvNilai.visibility = View.VISIBLE
                    }
                } else {
                    tvNilai.visibility = View.GONE
                }
                
                root.setOnClickListener {
                    onItemClick?.invoke(krsItem)
                }
            }
        }
    }
} 