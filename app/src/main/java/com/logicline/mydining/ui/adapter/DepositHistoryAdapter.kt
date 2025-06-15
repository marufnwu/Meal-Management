package com.logicline.mydining.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.databinding.LayoutDepositHistoryItemBinding
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.data.models.Deposit
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.LocalDB
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast

class DepositHistoryAdapter(val context: Context, val depositHistoryList: MutableList<Deposit>) : RecyclerView.Adapter<DepositHistoryAdapter.MyViewHolder>() {


    interface OnItemAction{
        fun onEdit(deposit: Deposit, position: Int)
    }

    var onItemAction : OnItemAction? = null

    inner class MyViewHolder(val binding: LayoutDepositHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Deposit){
            binding.txtDate.text = item.date
            binding.txtAmount.text = item.amount.toString()

            if(!AppPrefs.messUser.hasAnyPermission(MessPermission.DEPOSIT_MANAGEMENT)){
                binding.imgEdit.isEnabled = false
            }

            binding.imgEdit.setOnClickListener {

                if(AppPrefs.messUser.hasAnyPermission(MessPermission.DEPOSIT_MANAGEMENT)){
                    onItemAction?.onEdit(item, absoluteAdapterPosition)
                }else{
                    context.shortToast("You don't have access to edit or delete any data")
                }


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutDepositHistoryItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(depositHistoryList[position])
    }

    override fun getItemCount(): Int {
        return depositHistoryList.size
    }
}