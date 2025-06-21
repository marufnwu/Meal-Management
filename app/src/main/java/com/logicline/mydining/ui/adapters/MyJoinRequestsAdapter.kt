package com.logicline.mydining.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.ItemMyJoinRequestBinding
import com.logicline.mydining.data.models.response.UserJoinRequest
import java.text.SimpleDateFormat
import java.util.Locale

class MyJoinRequestsAdapter(
    private val onCancelClick: (UserJoinRequest) -> Unit
) : ListAdapter<UserJoinRequest, MyJoinRequestsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyJoinRequestBinding.inflate(
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
        private val binding: ItemMyJoinRequestBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(request: UserJoinRequest) {
            binding.apply {
                txtMessName.text = request.mess.name
                
                // Message field may not exist in API so use a placeholder
                txtMessage.text = "Join request" // Or hide this field if not needed
                
                // Format date
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                    val date = inputFormat.parse(request.created_at)
                    txtRequestDate.text = "Requested: ${outputFormat.format(date!!)}"
                } catch (e: Exception) {
                    txtRequestDate.text = "Requested: ${request.created_at}"
                }

                // Set status and color
                when (request.status.lowercase()) {
                    "pending" -> {
                        txtStatus.text = "PENDING"
                        txtStatus.setTextColor(Color.parseColor("#FF9800")) // Orange
                        btnCancel.visibility = android.view.View.VISIBLE
                    }
                    "accepted" -> {
                        txtStatus.text = "ACCEPTED"
                        txtStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
                        btnCancel.visibility = android.view.View.GONE
                    }
                    "rejected" -> {
                        txtStatus.text = "REJECTED"
                        txtStatus.setTextColor(Color.parseColor("#F44336")) // Red
                        btnCancel.visibility = android.view.View.GONE
                        
                        if (!request.rejection_reason.isNullOrBlank()) {
                            txtRejectionReason.text = "Reason: ${request.rejection_reason}"
                            txtRejectionReason.visibility = android.view.View.VISIBLE
                        } else {
                            txtRejectionReason.visibility = android.view.View.GONE
                        }
                    }
                    "cancelled" -> {
                        txtStatus.text = "CANCELLED"
                        txtStatus.setTextColor(Color.parseColor("#757575")) // Gray
                        btnCancel.visibility = android.view.View.GONE
                    }
                    else -> {
                        txtStatus.text = request.status.uppercase()
                        txtStatus.setTextColor(Color.parseColor("#757575")) // Gray
                        btnCancel.visibility = android.view.View.GONE
                    }
                }

                btnCancel.setOnClickListener {
                    onCancelClick(request)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<UserJoinRequest>() {
        override fun areItemsTheSame(oldItem: UserJoinRequest, newItem: UserJoinRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserJoinRequest, newItem: UserJoinRequest): Boolean {
            return oldItem.status == newItem.status &&
                   oldItem.rejection_reason == newItem.rejection_reason
        }
    }
}
