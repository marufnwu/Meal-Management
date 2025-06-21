package com.logicline.mydining.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.ItemAvailableMessBinding
import com.logicline.mydining.data.models.response.AvailableMess

class AvailableMessesAdapter(
    private val onMessClick: (AvailableMess) -> Unit
) : ListAdapter<AvailableMess, AvailableMessesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAvailableMessBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemAvailableMessBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(messItem: AvailableMess) {
            binding.apply {
                val mess = messItem.mess
                
                txtMessName.text = mess.name
                txtMemberCount.text = "${messItem.member_count} members"
                txtCreatedAt.text = "Created: ${formatDate(mess.created_at)}"
                
                // Note: Location and description are no longer in the API response
                // If you want to keep showing these fields, you'll need to update your API
                // or hide these fields from the UI
                txtLocation.text = "Location not specified"
                txtDescription.text = "No description available"
                
                when {
                    messItem.join_request_exists -> {
                        btnJoinMess.text = "Request Pending"
                        btnJoinMess.isEnabled = false
                        btnJoinMess.alpha = 0.6f
                    }
                    messItem.is_accepting_members -> {
                        btnJoinMess.text = "Send Join Request"
                        btnJoinMess.isEnabled = true
                        btnJoinMess.alpha = 1.0f
                    }
                    else -> {
                        btnJoinMess.text = "Not Accepting Members"
                        btnJoinMess.isEnabled = false
                        btnJoinMess.alpha = 0.6f
                    }
                }

                btnJoinMess.setOnClickListener {
                    onMessClick(messItem)
                }
            }
        }
        
        private fun formatDate(dateStr: String): String {
            // Simple date formatting function - you can use your existing formatDate logic
            return dateStr.split("T").firstOrNull() ?: dateStr
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AvailableMess>() {
        override fun areItemsTheSame(oldItem: AvailableMess, newItem: AvailableMess): Boolean {
            return oldItem.mess.id == newItem.mess.id
        }

        override fun areContentsTheSame(oldItem: AvailableMess, newItem: AvailableMess): Boolean {
            return oldItem == newItem
        }
    }
}
