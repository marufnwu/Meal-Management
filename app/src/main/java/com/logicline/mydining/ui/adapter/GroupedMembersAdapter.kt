package com.logicline.mydining.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.data.models.response.AllMembersResponse
import com.logicline.mydining.databinding.ItemGroupedMembersBinding

class GroupedMembersAdapter(
    private val context: Context,
    private val groupedMembers: List<AllMembersResponse>,
    private val onDeleteClick: ((com.logicline.mydining.data.models.MessUser) -> Unit)? = null
) : RecyclerView.Adapter<GroupedMembersAdapter.GroupedMembersViewHolder>() {

    inner class GroupedMembersViewHolder(private val binding: ItemGroupedMembersBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(group: AllMembersResponse) {
            binding.tvGroupTitle.text = group.status
            binding.tvUserCount.text = "${group.users.size} members"
            
            // Setup nested RecyclerView for users
            val userAdapter = UserListAdapter(context, group.users.toMutableList())
            userAdapter.onAction = object : UserListAdapter.OnAction {
                override fun onDeleteClick(messUser: com.logicline.mydining.data.models.MessUser) {
                    onDeleteClick?.invoke(messUser)
                }
            }
            
            binding.rvUsers.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = userAdapter
                setHasFixedSize(true)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupedMembersViewHolder {
        val binding = ItemGroupedMembersBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GroupedMembersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupedMembersViewHolder, position: Int) {
        holder.bind(groupedMembers[position])
    }

    override fun getItemCount(): Int = groupedMembers.size
} 