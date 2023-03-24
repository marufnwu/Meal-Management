package com.logicline.mydining.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityUserGuideBinding
import com.logicline.mydining.databinding.LayoutUserGuideItemBinding
import com.logicline.mydining.models.UserGuide

class UserGuideAdapter(val context:Context, val userGuides:MutableList<UserGuide>): RecyclerView.Adapter<UserGuideAdapter.MyViewHolder>() {

    interface OnAction{
        fun onClick(userGuide: UserGuide)
    }

    var onAction : OnAction? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutUserGuideItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return userGuides.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(userGuides[position])
    }

    inner class MyViewHolder(val binding: LayoutUserGuideItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(userGuide: UserGuide){
            Glide.with(context)
                .load(userGuide.thumb_url)
                .placeholder(R.drawable.loading)
                .into(binding.imgThumb)

            binding.txtTitle.text = userGuide.title

            binding.root.setOnClickListener {
                onAction?.onClick(userGuide)
            }
        }
    }
}