package com.logicline.mydining.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
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
                // Set user info
                tvName.text = userSummary.messUser.user?.name ?: "Unknown User"
                tvUserStatus.text = when {
                    userSummary.messUser.leftAt != null -> "Left Member"
                    else -> "Active Member"
                }

                // Set meal breakdown
                chipBreakfast.text = "B: ${userSummary.meals.breakfast}"
                chipLunch.text = "L: ${userSummary.meals.lunch}"
                chipDinner.text = "D: ${userSummary.meals.dinner}"
                chipTotalMeal.text = "${userSummary.meals.total} Meals"

                // Set financial data
                tvDeposit.text = formatCurrency(userSummary.deposit)
                tvMealCharge.text = formatCurrency(userSummary.mealCharge)
                tvOtherCost.text = formatCurrency(userSummary.otherCostShare)
                tvBalance.text = formatCurrency(userSummary.balance)
                tvDue.text = formatCurrency(userSummary.due)

                // Set colors based on status
                updateStatusColors(userSummary)
            }
        }

        private fun updateStatusColors(userSummary: UserSummaryItem) {
            with(binding) {
                // User status colors
                if (userSummary.messUser.leftAt != null == true) {
                    tvUserStatus.setTextColor(ContextCompat.getColor(context, R.color.gray_500))
                }

                // Balance/Due colors and visibility
                if (userSummary.balance >= 0) {
                    tvBalance.setTextColor(ContextCompat.getColor(context, R.color.green_700))
                    dueContainer.visibility = View.GONE
                } else {
                    tvBalance.setTextColor(ContextCompat.getColor(context, R.color.red_700))
                    tvDue.setTextColor(ContextCompat.getColor(context, R.color.red_700))
                    dueContainer.visibility = View.VISIBLE
                }
            }
        }

        private fun formatCurrency(amount: Float): String {
            return "৳${decimalFormat.format(amount)}"
        }
    }
}