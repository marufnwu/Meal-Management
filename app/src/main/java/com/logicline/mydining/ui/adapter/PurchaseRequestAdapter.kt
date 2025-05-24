package com.logicline.mydining.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.data.enums.PurchaseRequestStatus
import com.logicline.mydining.data.models.PurchaseRequest
import com.logicline.mydining.databinding.LayoutPurchaseRequestItemBinding

class PurchaseRequestAdapter(
    private val context: Context,
    private val items: MutableList<PurchaseRequest>,
    private val status: PurchaseRequestStatus
) : RecyclerView.Adapter<PurchaseRequestAdapter.RequestViewHolder>() {

    private var onActionClick: OnActionClick? = null
    private var onItemClick: ((PurchaseRequest) -> Unit)? = null

    interface OnActionClick {
        fun onAccept(requestId: Int, isDeposit: Int, purchaseType: Int, position: Int)
        fun onReject(requestId: Int, position: Int)
    }

    fun setOnActionClickListener(listener: OnActionClick) {
        this.onActionClick = listener
    }

    fun setOnItemClickListener(listener: (PurchaseRequest) -> Unit) {
        this.onItemClick = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder(
            LayoutPurchaseRequestItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RequestViewHolder(private val binding: LayoutPurchaseRequestItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(items[position])
                }
            }
        }

        fun bind(request: PurchaseRequest) {
            binding.txtDate.text = request.date
            binding.txtName.text = request.name
            binding.txtTotalPrice.text = String.format("$%.2f", request.price)

            val s = PurchaseRequestStatus.fromValue(request.status)

            // Set status indicator
            when (s) {
                PurchaseRequestStatus.PENDING  -> binding.imgStatus.setImageResource(R.drawable.pending_24px)
                PurchaseRequestStatus.APPROVED -> binding.imgStatus.setImageResource(R.drawable.check_24px)
                PurchaseRequestStatus.REJECTED -> binding.imgStatus.setImageResource(R.drawable.rejected_24px)
                else -> {}
            }
        }
    }
}