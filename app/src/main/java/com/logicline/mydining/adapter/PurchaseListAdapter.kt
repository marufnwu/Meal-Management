package com.logicline.mydining.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.LayoutPurchaseItemBinding
import com.logicline.mydining.models.Purchase
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.MyExtensions.shortToast


class PurchaseListAdapter(val context: Context, val purchaseList : List<Purchase>) : RecyclerView.Adapter<PurchaseListAdapter.ViewHolder>() {


    interface OnAction{
        fun onItemClick(purchase: Purchase, position: Int)
    }

    var onAction : OnAction? = null

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

            binding.root.setOnClickListener {
                if(Constant.isManagerOrSuperUser()){
                    onAction?.onItemClick(purchase, absoluteAdapterPosition)
                }
            }
        }
    }

}