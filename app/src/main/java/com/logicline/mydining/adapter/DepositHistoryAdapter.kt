package com.logicline.mydining.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.databinding.LayoutDepositHistoryItemBinding
import com.logicline.mydining.models.DepositHistory
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.MyExtensions.shortToast

class DepositHistoryAdapter(val context: Context ,val depositHistoryList: List<DepositHistory>) : RecyclerView.Adapter<DepositHistoryAdapter.MyViewHolder>() {


    interface OnItemAction{
        fun onEdit(depositHistory: DepositHistory, position: Int)
    }

    var onItemAction : OnItemAction? = null

    inner class MyViewHolder(val binding: LayoutDepositHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DepositHistory){
            binding.txtDate.text = item.date
            binding.txtAmount.text = item.amount.toString()

            if(!Constant.isManagerOrSuperUser()){
                binding.imgEdit.setImageResource(R.drawable.cross)
            }

            binding.imgEdit.setOnClickListener {

                if(Constant.isManagerOrSuperUser()){
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