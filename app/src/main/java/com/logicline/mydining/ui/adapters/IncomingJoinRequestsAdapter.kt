package com.logicline.mydining.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.ItemIncomingJoinRequestBinding
import com.logicline.mydining.data.models.response.IncomingJoinRequest
import java.text.SimpleDateFormat
import java.util.Locale

class IncomingJoinRequestsAdapter(
    private val onAcceptClick: (IncomingJoinRequest) -> Unit,
    private val onRejectClick: (IncomingJoinRequest) -> Unit
) : ListAdapter<IncomingJoinRequest, IncomingJoinRequestsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIncomingJoinRequestBinding.inflate(
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
        private val binding: ItemIncomingJoinRequestBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(request: IncomingJoinRequest) {
            binding.apply {
                txtUserName.text = request.user.name
                txtUserEmail.text = request.user.email
                
                // Phone and city may not be in the API response anymore
                // Consider hiding these fields if not available
                txtUserPhone.visibility = android.view.View.GONE
                txtUserCity.visibility = android.view.View.GONE
                
                // Message field from join request
                txtMessage.text = request.message ?: "No message provided"
                
                // Format date
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                    val date = inputFormat.parse(request.created_at)
                    txtRequestDate.text = "Requested: ${outputFormat.format(date!!)}"
                } catch (e: Exception) {
                    txtRequestDate.text = "Requested: ${request.created_at}"
                }

                // User background information is not in the API anymore
                // Hide these fields
                txtExperience.visibility = android.view.View.GONE
                txtDietaryRestrictions.visibility = android.view.View.GONE

                // Only show action buttons for pending requests
                if (request.status.lowercase() == "pending") {
                    layoutActions.visibility = android.view.View.VISIBLE
                    
                    btnAccept.setOnClickListener {
                        onAcceptClick(request)
                    }
                    
                    btnReject.setOnClickListener {
                        onRejectClick(request)
                    }
                } else {
                    layoutActions.visibility = android.view.View.GONE
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<IncomingJoinRequest>() {
        override fun areItemsTheSame(oldItem: IncomingJoinRequest, newItem: IncomingJoinRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IncomingJoinRequest, newItem: IncomingJoinRequest): Boolean {
            return oldItem.status == newItem.status
        }
    }
}
