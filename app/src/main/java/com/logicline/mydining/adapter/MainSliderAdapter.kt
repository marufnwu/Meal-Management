package com.logicline.mydining.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.google.android.gms.common.internal.service.Common
import com.logicline.mydining.R
import com.logicline.mydining.databinding.LayoutMainSliderItemBinding
import com.logicline.mydining.models.UserGuide
import com.logicline.mydining.utils.Constant
import com.smarteist.autoimageslider.SliderViewAdapter

class MainSliderAdapter(val context: Context, val userGuides: MutableList<UserGuide>) : SliderViewAdapter<MainSliderAdapter.MyViewHolder>() {
    inner class MyViewHolder(val binding: LayoutMainSliderItemBinding) : SliderViewAdapter.ViewHolder(binding.root){
        fun bind(userGuide: UserGuide){
            Glide.with(context)
                .load(userGuide.thumb_url)
                .into(binding.imgBg)

            binding.root.setOnClickListener {

                Constant.openLink(context, userGuide.action_url)
            }


        }
    }

    override fun getCount(): Int {
        return userGuides.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MyViewHolder {
        return MyViewHolder(LayoutMainSliderItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
        viewHolder?.bind(userGuides[position])
    }
}