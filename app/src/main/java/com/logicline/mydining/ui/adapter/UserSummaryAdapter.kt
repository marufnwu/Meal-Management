package com.logicline.mydining.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.databinding.LayoutSummaryItemBinding
import com.logicline.mydining.data.models.UserSummaryItem
import java.text.DecimalFormat

class UserSummaryAdapter(
    private val context: Context,
    private val userSummaryList: List<UserSummaryItem>
) : RecyclerView.Adapter<UserSummaryAdapter.ViewHolder>() {

    private val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutSummaryItemBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userSummaryList[position])
    }

    override fun getItemCount(): Int = userSummaryList.size

    inner class ViewHolder(private val binding: LayoutSummaryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userSummary: UserSummaryItem) {
            with(binding) {
                // Set user name (you might need to get this from a user repository)
                name.text = "User ${userSummary.messUser.userId}" // Placeholder - replace with actual user name

                // Set meal data
                meal.text = userSummary.meals.total.toString()
                mealCost.text = formatCurrency(userSummary.mealCharge)

                // Set financial data
                deposit.text = formatCurrency(userSummary.deposit)
                otherCost.text = formatCurrency(userSummary.otherCostShare)
                totalCost.text = formatCurrency(userSummary.totalCost)

                // Set due/balance
                val dueAmount = if (userSummary.due > 0) userSummary.due else 0f
                due.text = formatCurrency(dueAmount)

                // Set status color based on balance/due
                setStatusColors(userSummary)
            }
        }

        private fun setStatusColors(userSummary: UserSummaryItem) {
            with(binding) {
                when {
                    userSummary.balance > 0 -> {
                        // User has positive balance (overpaid)
                        due.setTextColor(context.getColor(android.R.color.holo_green_dark))
                        name.setBackgroundResource(com.logicline.mydining.R.color.success_container)
                    }
                    userSummary.due > 0 -> {
                        // User has due amount
                        due.setTextColor(context.getColor(android.R.color.holo_red_dark))
                        name.setBackgroundResource(com.logicline.mydining.R.color.error_container)
                    }
                    else -> {
                        // User is balanced
                        due.setTextColor(context.getColor(android.R.color.darker_gray))
                    }
                }
            }
        }

        private fun formatCurrency(amount: Float): String {
            return "৳${decimalFormat.format(amount)}"
        }
    }
}