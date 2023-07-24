package com.logicline.mydining.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.databinding.LayoutReportItemBinding
import com.logicline.mydining.models.Report
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.MyDownloadManager


class ReportAdapter(val context:Context, val items: MutableList<Report>) : RecyclerView.Adapter<ReportAdapter.MyViewHolder>() {


    var isAnimate = false


    var onAction: ((report:Report)->Unit)? = null


    inner class MyViewHolder(val binding: LayoutReportItemBinding) : RecyclerView.ViewHolder(binding.root){
          @SuppressLint("SetTextI18n")
          fun bind(report: Report){
              binding.txtDate.text =
                  "${report.creation_date.split(" ")[0]}\n${report.creation_date.split(" ")[1]}"

              binding.txtMonth.text = Constant.getMonthName("2000-${report.month}-01")
              binding.txtYear.text  =report.year.toString()

              binding.imgDownload.setOnClickListener {
                  onAction?.invoke(report)
              }
          }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutReportItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
        if(position==0 && isAnimate){
            setScaleAnimation(holder.itemView)
            isAnimate = false
        }

    }

    fun addNewList(newList : MutableList<Report>){
        items.addAll(0, newList)
        notifyItemChanged(0)
    }

    private fun setScaleAnimation(view: View) {
        val anim = ScaleAnimation(
            0.0f,
            1.0f,
            0.0f,
            1.0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        anim.duration = 500
        view.startAnimation(anim)
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 1000
        view.startAnimation(anim)
    }
}