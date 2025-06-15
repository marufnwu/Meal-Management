package com.logicline.mydining.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.ui.adapter.holders.AdViewHolder
import com.logicline.mydining.databinding.LayoutDepositItemBinding
import com.logicline.mydining.databinding.LayoutNativeAdViewBinding
import com.logicline.mydining.data.models.DepositSum
import com.logicline.mydining.utils.Constant

class DepositListAdapter(val context: Context, val depositList: MutableList<DepositSum>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener{
        fun onClick(deposit : DepositSum)
    }

    var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         if(viewType == Constant.VIEW_TYPE.NATIVE_AD_ITEM.ordinal){
            return AdViewHolder(
                LayoutNativeAdViewBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
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
        fun bind(deposit: DepositSum){
//            deposit.photoUrl?.let { url->
//                Glide.with(context)
//                    .load(url)
//                    .into(binding.profileImage)
//            }

            binding.txtTitle.text = deposit.messUser.user?.name
            binding.txtValue.text = deposit.totalAmount.toString() + " /="

            binding.root.setOnClickListener {
                onItemClickListener?.onClick(deposit)
            }

        }
    }
}