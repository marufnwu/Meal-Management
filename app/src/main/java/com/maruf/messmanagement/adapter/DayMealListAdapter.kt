package com.maruf.messmanagement.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.maruf.messmanagement.R
import com.maruf.messmanagement.databinding.LayoutDayMealBinding
import com.maruf.messmanagement.databinding.LayoutMemberMealBinding
import com.maruf.messmanagement.models.Meal
import com.maruf.messmanagement.utils.Constant

class DayMealListAdapter(val context: Context, val dayList : List<List<Meal>>): RecyclerView.Adapter<DayMealListAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutDayMealBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dayList[position], position)
    }

    override fun getItemCount(): Int {

       return dayList.size
    }

    inner class ViewHolder(val binding: LayoutDayMealBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(dayList: List<Meal>, pos: Int){
            Log.d("Position", pos.toString())
            binding.txtDate.text = dayList[0].date
            binding.txtDay.text = Constant.getDayNameFromDate(dayList[0].date!!)

            binding.recyMealList.setHasFixedSize(true)
            binding.recyMealList.layoutManager = LinearLayoutManager(context)
            val adapter = MemberMealListAdapter(context, dayList)
            binding.recyMealList.adapter = adapter
        }
    }
}

class MemberMealListAdapter(val context: Context, val memberList : List<Meal>): RecyclerView.Adapter<MemberMealListAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutMemberMealBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(memberList[position], position)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    inner class ViewHolder(val binding: LayoutMemberMealBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Meal, pos :Int){
            binding.name.text = meal.name
            binding.dinner.text = meal.dinner
            binding.morning.text = meal.breakfast
            binding.lunch.text = meal.lunch

            if(pos%2==0){
                binding.name.setTextColor(context.getColor(R.color.White))
                binding.dinner.setTextColor(context.getColor(R.color.White))
                binding.morning.setTextColor(context.getColor(R.color.White))
                binding.lunch.setTextColor(context.getColor(R.color.White))
            }else{
                binding.name.setTextColor(context.getColor(R.color.Black))
                binding.dinner.setTextColor(context.getColor(R.color.Black))
                binding.morning.setTextColor(context.getColor(R.color.Black))
                binding.lunch.setTextColor(context.getColor(R.color.Black))
            }
        }
    }
}

