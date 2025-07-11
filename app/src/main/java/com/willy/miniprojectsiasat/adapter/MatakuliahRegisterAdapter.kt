package com.willy.miniprojectsiasat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willy.miniprojectsiasat.databinding.ItemMatakuliahRegisterBinding
import com.willy.miniprojectsiasat.model.Matakuliah
import com.willy.miniprojectsiasat.model.User

data class MatakuliahRegisterItem(
    val matakuliah: Matakuliah,
    val dosenName: String = "",
    val isRegistered: Boolean = false
)

class MatakuliahRegisterAdapter(
    private val matakuliahList: MutableList<MatakuliahRegisterItem> = mutableListOf(),
    private val onRegisterClick: ((Matakuliah) -> Unit)? = null
) : RecyclerView.Adapter<MatakuliahRegisterAdapter.MatakuliahRegisterViewHolder>() {

    fun updateData(newMatakuliahList: List<MatakuliahRegisterItem>) {
        matakuliahList.clear()
        matakuliahList.addAll(newMatakuliahList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatakuliahRegisterViewHolder {
        val binding = ItemMatakuliahRegisterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MatakuliahRegisterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatakuliahRegisterViewHolder, position: Int) {
        holder.bind(matakuliahList[position])
    }

    override fun getItemCount(): Int = matakuliahList.size

    inner class MatakuliahRegisterViewHolder(
        private val binding: ItemMatakuliahRegisterBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MatakuliahRegisterItem) {
            binding.apply {
                val matakuliah = item.matakuliah
                
                tvKodeMK.text = matakuliah.id
                tvNamaMK.text = matakuliah.nama
                tvSKS.text = matakuliah.sks.toString()
                tvDosenPengampu.text = item.dosenName.ifEmpty { "Belum ada dosen" }
                
                // Format jadwal
                val jadwalText = "${matakuliah.jadwal.hari}, ${matakuliah.jadwal.jam_mulai}-${matakuliah.jadwal.jam_selesai} (${matakuliah.jadwal.ruangan})"
                tvJadwal.text = jadwalText
                
                // Status registrasi
                if (item.isRegistered) {
                    tvStatus.visibility = View.VISIBLE
                    tvStatus.text = "Terdaftar"
                    btnRegister.text = "SUDAH TERDAFTAR"
                    btnRegister.isEnabled = false
                } else {
                    tvStatus.visibility = View.GONE
                    btnRegister.text = "DAFTAR MATAKULIAH"
                    btnRegister.isEnabled = true
                }
                
                btnRegister.setOnClickListener {
                    if (!item.isRegistered) {
                        onRegisterClick?.invoke(matakuliah)
                    }
                }
            }
        }
    }
} 