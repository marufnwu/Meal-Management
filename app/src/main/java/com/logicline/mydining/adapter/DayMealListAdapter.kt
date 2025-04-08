package com.logicline.mydining.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.adapter.holders.AdViewHolder
import com.logicline.mydining.databinding.LayoutDayMealBinding
import com.logicline.mydining.databinding.LayoutMemberMealBinding
import com.logicline.mydining.databinding.LayoutNativeAdViewBinding
import com.logicline.mydining.enums.MessPermission
import com.logicline.mydining.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.models.Meal
import com.logicline.mydining.models.MealDate
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LocalDB


class DayMealListAdapter(val context: Context, val dayList: MutableList<MealDate>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {




    interface OnAction{
        fun onClick(meal: Meal, pos: Int)
    }

    var onAction: OnAction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == Constant.VIEW_TYPE.NATIVE_AD_ITEM.ordinal){
            return AdViewHolder(LayoutNativeAdViewBinding.inflate(LayoutInflater.from(context), parent, false))
        }else{
            return ViewHolder(LayoutDayMealBinding.inflate(LayoutInflater.from(context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(holder is ViewHolder){
            if(position>=2){
                holder.bind(dayList[position-1], position-1)
            }else{
                holder.bind(dayList[position], position)
            }
        }

    }

    override fun getItemCount(): Int {

        return if(dayList.isNotEmpty()){
            dayList.size+1
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

    inner class ViewHolder(val binding: LayoutDayMealBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(day: MealDate, pos: Int){
            Log.d("Position", pos.toString())
            binding.txtDate.text = day.date
            binding.txtDay.text = Constant.getDayNameFromDate(day.date)

            binding.recyMealList.setHasFixedSize(true)
            binding.recyMealList.layoutManager = LinearLayoutManager(context)
            val adapter =
                MemberMealListAdapter(
                    context,
                    day.meals
                )
            binding.recyMealList.adapter = adapter
            adapter.onAction = object : MemberMealListAdapter.OnAction {
                override fun onClick(meal: Meal, pos: Int) {
                    onAction?.onClick(meal, pos)
                }

            }
        }
    }
}

class MemberMealListAdapter(val context: Context, val memberList : List<Meal>): RecyclerView.Adapter<MemberMealListAdapter.ViewHolder>() {

    public interface OnAction{
        fun onClick(meal: Meal, pos: Int)
    }

    var onAction : OnAction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberMealListAdapter.ViewHolder {
        return ViewHolder(LayoutMemberMealBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder:MemberMealListAdapter.ViewHolder, position: Int) {
        holder.bind(memberList[position], position)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    inner class ViewHolder(val binding: LayoutMemberMealBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(meal: Meal, pos :Int){
            binding.name.text = meal.messUser?.user?.name
            binding.dinner.text = meal.dinner.toString()
            binding.morning.text = meal.breakfast.toString()
            binding.lunch.text = meal.lunch.toString()

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

            binding.root.setOnClickListener {
                if(LocalDB.getUserData()?.messUser.hasAnyPermission(MessPermission.MEAL_EDIT, MessPermission.MEAL_MANAGEMENT)){
                    onAction?.onClick(meal, absoluteAdapterPosition)
                }
            }

        }
    }
}

