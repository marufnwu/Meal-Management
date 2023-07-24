package com.logicline.mydining.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.adapter.holders.AdViewHolder
import com.logicline.mydining.databinding.LayoutMonthItemBinding
import com.logicline.mydining.databinding.LayoutNativeAdViewBinding
import com.logicline.mydining.models.MonthOfYear
import com.logicline.mydining.utils.Constant

class MonthAdapter(val context: Context,val  months:MutableList<MonthOfYear>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var onClick : ((month:MonthOfYear)->Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType==Constant.VIEW_TYPE.NORMAL_ITEM.ordinal){
            MyViewHolder(LayoutMonthItemBinding.inflate(LayoutInflater.from(context), parent, false))
        }else{
             AdViewHolder(LayoutNativeAdViewBinding.inflate(LayoutInflater.from(context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is MyViewHolder){
            if(position>=2){
                holder.bind(months[position-1])
            }else{
                holder.bind(months[position])
            }

        }
    }

    override fun getItemCount(): Int {
        return if (months.isNotEmpty()){
            months.size+1
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

    inner class MyViewHolder(val binding:LayoutMonthItemBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(month:MonthOfYear){
            binding.txtMonth.text = "${month.monthName} ${month.year}"

            binding.root.setOnClickListener {
                onClick?.invoke(month)
            }
        }

    }
}