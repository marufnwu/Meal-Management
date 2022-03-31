package com.maruf.messmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maruf.messmanagement.databinding.LayoutPurchaseItemBinding
import com.maruf.messmanagement.models.Purchase

class PurchaseListAdapter(val context: Context, val purchaseList : List<Purchase>) : RecyclerView.Adapter<PurchaseListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseListAdapter.ViewHolder {
        return ViewHolder(LayoutPurchaseItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: PurchaseListAdapter.ViewHolder, position: Int) {
        holder.bind(purchaseList[position])
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }
    inner class ViewHolder(private val binding : LayoutPurchaseItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(purchase: Purchase){
            binding.txtDate.text = purchase.date
            binding.txtName.text = purchase.user
            binding.txtProduct.text = if (purchase.product.length>10) purchase.product.substring(0, 10)+".." else purchase.product
            binding.txtPrice.text = purchase.price
        }
    }

}