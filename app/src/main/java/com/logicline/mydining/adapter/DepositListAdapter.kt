package com.logicline.mydining.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.adapter.holders.AdViewHolder
import com.logicline.mydining.databinding.LayoutDepositItemBinding
import com.logicline.mydining.databinding.LayoutNativeAdViewBinding
import com.logicline.mydining.models.Deposit
import com.logicline.mydining.ui.DepositHistoryActivity
import com.logicline.mydining.utils.Constant

class DepositListAdapter(val context: Context, val depositList : MutableList<Deposit>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener{
        fun onClick(userId: String, messId:String)
    }

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         if(viewType == Constant.VIEW_TYPE.NATIVE_AD_ITEM.ordinal){
            return AdViewHolder(LayoutNativeAdViewBinding.inflate(LayoutInflater.from(context), parent, false))
        }else{
             return ViewHolder(LayoutDepositItemBinding.inflate(LayoutInflater.from(context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder){
            if(position>=2){
                holder.bind(depositList[position-1])
            }else{
                holder.bind(depositList[position])
            }

        }else if(holder is AdViewHolder){

        }
    }

    override fun getItemCount(): Int {
        return if(depositList.isNotEmpty()){
            depositList.size+1
        }else{
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position==1){
            return Constant.VIEW_TYPE.NATIVE_AD_ITEM.ordinal
        }

        return Constant.VIEW_TYPE.NORMAL_ITEM.ordinal
    }

    inner class ViewHolder(val binding: LayoutDepositItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(deposit: Deposit){
//            deposit.photoUrl?.let { url->
//                Glide.with(context)
//                    .load(url)
//                    .into(binding.profileImage)
//            }

            binding.txtName.text = deposit.name
            binding.txtAmount.text = deposit.amount+" /="

            binding.root.setOnClickListener {
                onItemClickListener?.onClick(deposit.userId, deposit.messId)
            }

        }
    }
}