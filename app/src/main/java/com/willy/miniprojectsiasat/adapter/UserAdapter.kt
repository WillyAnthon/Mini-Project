package com.willy.miniprojectsiasat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.willy.miniprojectsiasat.databinding.ItemUserBinding
import com.willy.miniprojectsiasat.model.User

class UserAdapter(
    private val userList: MutableList<User> = mutableListOf(),
    private val onItemClick: ((User) -> Unit)? = null
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    fun updateData(newUserList: List<User>) {
        userList.clear()
        userList.addAll(newUserList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size

    inner class UserViewHolder(
        private val binding: ItemUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                tvUserId.text = user.id
                tvUserName.text = user.nama
                tvUserRole.text = user.role.replaceFirstChar { it.uppercase() }
                
                root.setOnClickListener {
                    onItemClick?.invoke(user)
                }
            }
        }
    }
} 