package com.maruf.messmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maruf.messmanagement.R
import com.maruf.messmanagement.databinding.LayoutDepositItemBinding
import com.maruf.messmanagement.databinding.LayoutUserItemBinding
import com.maruf.messmanagement.models.User
import com.maruf.messmanagement.models.response.Deposit

class DepositListAdapter(val context: Context, val depositList : MutableList<Deposit>) : RecyclerView.Adapter<DepositListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutDepositItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(depositList[position])
    }

    override fun getItemCount(): Int {
        return  depositList.size
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

        }
    }
}