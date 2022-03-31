package com.maruf.messmanagement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.maruf.messmanagement.databinding.LayoutSummaryItemBinding
import com.maruf.messmanagement.models.response.UsersSummary

class UserSummaryAdapter(val context: Context, val userSummaryList:List<UsersSummary>) : RecyclerView.Adapter<UserSummaryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutSummaryItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userSummaryList[position])
    }

    override fun getItemCount(): Int {
        return userSummaryList.size
    }
    inner class ViewHolder(val binding : LayoutSummaryItemBinding ):RecyclerView.ViewHolder(binding.root) {
        fun bind(userSummary: UsersSummary){

            binding.name.text = userSummary.user!!.name

            binding.deposit.text = userSummary.deposit.toString()
            binding.meal.text = userSummary.meal.toString()
            binding.totalCost.text = userSummary.totalCost.toString()
            binding.otherCost.text = userSummary.otherCost.toString()
            binding.due.text = userSummary.due.toString()
            binding.mealCost.text = userSummary.mealCost.toString()
        }
    }

}