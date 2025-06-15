package com.logicline.mydining.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.data.models.PurchaseRequest
import com.logicline.mydining.databinding.LayoutPurchaseDetailsItemBinding

class PurchaseDetailAdapter :
    ListAdapter<PurchaseRequest.ProductItem, PurchaseDetailAdapter.ProductViewHolder>(
        ProductDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(
            LayoutPurchaseDetailsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: LayoutPurchaseDetailsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: PurchaseRequest.ProductItem) {
            binding.tvProductName.text = product.name ?: "Unknown Product"
            binding.tvProductQuantity.text = "x${product.quantity}"
            binding.tvProductPrice.text =
                String.format("$%.2f", product.unitPrice * product.quantity)
        }
    }

    private class ProductDiffCallback : DiffUtil.ItemCallback<PurchaseRequest.ProductItem>() {
        override fun areItemsTheSame(
            oldItem: PurchaseRequest.ProductItem,
            newItem: PurchaseRequest.ProductItem
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: PurchaseRequest.ProductItem,
            newItem: PurchaseRequest.ProductItem
        ): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.quantity == newItem.quantity &&
                    oldItem.unitPrice == newItem.unitPrice
        }
    }
}