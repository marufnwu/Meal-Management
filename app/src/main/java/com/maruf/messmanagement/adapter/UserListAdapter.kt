package com.maruf.messmanagement.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maruf.messmanagement.R
import com.maruf.messmanagement.databinding.LayoutUserItemBinding
import com.maruf.messmanagement.models.User
import com.maruf.messmanagement.ui.ProfileActivity

class UserListAdapter(val context: Context, val userList : MutableList<User>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutUserItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int {
        return  userList.size
    }

    inner class ViewHolder(val binding: LayoutUserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User){
            user.photoUrl?.let { url->
                Glide.with(context)
                    .load(url)
                    .into(binding.profileImage)
            }

            binding.txtName.text = user.name

            if(user.active=="1"){
                Glide.with(context)
                    .load(R.drawable.check)
                    .into(binding.active)
            }

            binding.root.setOnClickListener {
                context.startActivity(Intent(context, ProfileActivity::class.java).putExtra("profile", user))
            }
        }
    }
}