package com.logicline.mydining.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.LayoutPurchaseItemBinding
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.data.models.Purchase
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.LocalDB


class PurchaseListAdapter(val context: Context, val purchaseList : List<Purchase>) : RecyclerView.Adapter<PurchaseListAdapter.ViewHolder>() {


    interface OnAction{
        fun onItemClick(purchase: Purchase, position: Int)
    }

    var onAction : OnAction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutPurchaseItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(purchaseList[position])
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }
    inner class ViewHolder(private val binding : LayoutPurchaseItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(purchase: Purchase){
            binding.txtDate.text = purchase.date
            binding.txtName.text = purchase.messUSer.user?.name
            binding.txtProduct.text = if (purchase.product.length>10) purchase.product.substring(0, 10)+".." else purchase.product
            binding.txtPrice.text = purchase.price.toString()

            binding.imgEdit.setOnClickListener {
                if(AppPrefs.messUser.hasAnyPermission(MessPermission.PURCHASE_MANAGEMENT, MessPermission.PURCHASE_DELETE, MessPermission.PURCHASE_EDIT)){
                    onAction?.onItemClick(purchase, absoluteAdapterPosition)
                }
            }
        }
    }

}