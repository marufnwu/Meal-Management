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

        fun bind(mess: AvailableMess) {
            binding.apply {
                txtMessName.text = mess.name
                txtMemberCount.text = "${mess.member_count} members"
                txtLocation.text = mess.location ?: "Location not specified"
                txtDescription.text = mess.description ?: "No description available"
                
                when {
                    mess.join_request_exists -> {
                        btnJoinMess.text = "Request Pending"
                        btnJoinMess.isEnabled = false
                        btnJoinMess.alpha = 0.6f
                    }
                    mess.is_accepting_members -> {
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
                    onMessClick(mess)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AvailableMess>() {
        override fun areItemsTheSame(oldItem: AvailableMess, newItem: AvailableMess): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AvailableMess, newItem: AvailableMess): Boolean {
            return oldItem == newItem
        }
    }
}
