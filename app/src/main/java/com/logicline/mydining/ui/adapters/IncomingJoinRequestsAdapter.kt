package com.logicline.mydining.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.data.enums.MessJoinRequestStatus
import com.logicline.mydining.databinding.ItemIncomingJoinRequestBinding
import com.logicline.mydining.data.models.response.IncomingJoinRequest
import com.logicline.mydining.data.models.response.UserJoinRequest
import java.text.SimpleDateFormat
import java.util.Locale

class IncomingJoinRequestsAdapter(
    private val onAcceptClick: (UserJoinRequest) -> Unit,
    private val onRejectClick: (UserJoinRequest) -> Unit
) : ListAdapter<UserJoinRequest, IncomingJoinRequestsAdapter.ViewHolder>(DiffCallback()) {

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

        fun bind(request: UserJoinRequest) {
            binding.apply {
                txtUserName.text = request.user?.name
                txtUserEmail.text = request.user?.email
                txtUserPhone.text = request.user?.phone

                // Phone and city may not be in the API response anymore
                // Consider hiding these fields if not available
                txtUserPhone.visibility = View.GONE
                txtUserCity.visibility = View.GONE
                
                // Message field - IncomingJoinRequest doesn't have message field
                // Hide message field for now
                txtMessage.visibility = View.GONE
                
                // Format date
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                    txtRequestDate.text = "Requested: ${request.created_at.toDisplayDate()}"
                } catch (e: Exception) {
                    txtRequestDate.text = "Requested: ${request.created_at}"
                }

                // User background information is not in the API anymore
                // Hide these fields
                txtExperience.visibility = View.GONE
                txtDietaryRestrictions.visibility = View.GONE

                // Only show action buttons for pending requests
                if (request.status == MessJoinRequestStatus.PENDING.value) {
                    layoutActions.visibility = View.VISIBLE
                    
                    btnAccept.setOnClickListener {
                        onAcceptClick(request)
                    }
                    
                    btnReject.setOnClickListener {
                        onRejectClick(request)
                    }
                } else {
                    layoutActions.visibility = View.GONE
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<UserJoinRequest>() {
        override fun areItemsTheSame(oldItem: UserJoinRequest, newItem: UserJoinRequest): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserJoinRequest, newItem: UserJoinRequest): Boolean {
            return oldItem.status == newItem.status
        }
    }
}
