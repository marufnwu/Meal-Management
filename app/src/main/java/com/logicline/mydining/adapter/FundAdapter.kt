package com.logicline.mydining.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.LayoutFundItemBinding
import com.logicline.mydining.databinding.LayoutPurchaseItemBinding
import com.logicline.mydining.models.Fund
import com.logicline.mydining.utils.Constant

class FundAdapter(val context: Context, val items:MutableList<Fund>,val onEdit: ((Fund)-> Unit)?) : RecyclerView.Adapter<FundAdapter.MyViewHolder>() {



    inner class MyViewHolder(val binding: LayoutFundItemBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            if(!Constant.isManagerOrSuperUser()){
                binding.imgEdit.visibility = View.GONE
            }
        }
        fun bind(fund: Fund){
            binding.txtAmount.text = fund.amount.toString()
            binding.txtComment.text = fund.comment.toString()
            binding.txtDate.text = fund.date

            if (Constant.isManagerOrSuperUser()){
                binding.imgEdit.setOnClickListener {
                    onEdit?.invoke(fund)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutFundItemBinding.inflate(LayoutInflater.from(context), parent, false))

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }
}