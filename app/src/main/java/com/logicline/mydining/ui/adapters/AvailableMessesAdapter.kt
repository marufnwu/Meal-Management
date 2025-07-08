package com.logicline.mydining.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.ItemAvailableMessBinding
import com.logicline.mydining.data.models.response.AvailableMess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvailableMessesAdapter(
    private val onMessClick: (AvailableMess) -> Unit,
    private val onCancelRequest: (AvailableMess) -> Unit
) : RecyclerView.Adapter<AvailableMessesAdapter.ViewHolder>() {

    private var messList: List<AvailableMess> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAvailableMessBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messList[position])
    }

    override fun getItemCount(): Int = messList.size

    fun submitList(newList: List<AvailableMess>) {
        messList = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(
        private val binding: ItemAvailableMessBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(messItem: AvailableMess) {
            binding.apply {
                val mess = messItem.mess

                txtMessName.text = mess.name
                txtMemberCount.text = "${messItem.member_count} members"
                
                // Format and display creation date if available
                if (mess.createdAt != null) {
                    txtLocation.text = "Created: ${mess.createdAt.toDisplayDate()}"
                } else {
                    txtLocation.text = "Recently created"
                }

                // Show mess status and accepting members info
                val statusText = when {
                    mess.status.equals("active", ignoreCase = true) && messItem.is_accepting_members -> 
                        "Active • Accepting new members"
                    mess.status.equals("active", ignoreCase = true) -> 
                        "Active • Not accepting members"
                    else -> 
                        "Status: ${mess.status.replaceFirstChar { it.uppercase() }}"
                }
                txtDescription.text = statusText

                // Handle button state based on mess status and join request
                when {
                    messItem.join_request_exists -> {
                        btnJoinMess.text = "Cancel Request"
                        btnJoinMess.isEnabled = true
                        btnJoinMess.alpha = 1.0f
                    }
                    !messItem.is_accepting_members -> {
                        btnJoinMess.text = "Not Accepting Members"
                        btnJoinMess.isEnabled = false
                        btnJoinMess.alpha = 0.6f
                    }
                    !mess.status.equals("active", ignoreCase = true) -> {
                        btnJoinMess.text = "Mess Inactive"
                        btnJoinMess.isEnabled = false
                        btnJoinMess.alpha = 0.6f
                    }
                    else -> {
                        btnJoinMess.text = "Send Join Request"
                        btnJoinMess.isEnabled = true
                        btnJoinMess.alpha = 1.0f
                    }
                }

                btnJoinMess.setOnClickListener {
                    if (btnJoinMess.isEnabled) {
                        if (messItem.join_request_exists) {
                            onCancelRequest(messItem)
                        } else {
                            onMessClick(messItem)
                        }
                    }
                }
            }
        }

        private fun formatDate(dateStr: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = inputFormat.parse(dateStr)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                // If parsing fails, try to extract just the date part
                dateStr.split("T").firstOrNull()?.let { datePart ->
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val date = inputFormat.parse(datePart)
                        outputFormat.format(date ?: Date())
                    } catch (e2: Exception) {
                        datePart
                    }
                } ?: dateStr
            }
        }
    }
}
