package com.logicline.mydining.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.logicline.mydining.R
import com.logicline.mydining.databinding.MemberInitiateLayoutBinding
import com.logicline.mydining.models.User


class UserInitiateAdapter(val context: Context, val users: MutableList<User>, val type: Type) : RecyclerView.Adapter<UserInitiateAdapter.MyViewHolder>() {
    var onActionClick : OnActionClick? = null
    enum class Type{
        INITIATE, NOT_INITIATE
    }

    interface OnActionClick{
        fun onClick(user: User)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(MemberInitiateLayoutBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @JvmName("setOnActionClick1")
    fun setOnActionClick(onActionC: OnActionClick){
        this.onActionClick = onActionC
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class MyViewHolder(val binding: MemberInitiateLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User){
            Glide.with(context)
                .load(user.photoUrl)
                .placeholder(R.drawable.man)
                .into(binding.profileImage)

            binding.txtName.text = user.name
            if(type==Type.INITIATE){
                if(user.active!="1"){
                    binding.action.setImageResource(R.drawable.cross)
                }else{
                    binding.action.setImageResource(R.drawable.tick)
                }
            }else{
                binding.action.setImageResource(R.drawable.plus)
                binding.action.setOnClickListener {
                    onActionClick?.onClick(user)
                }
            }

        }
    }
}