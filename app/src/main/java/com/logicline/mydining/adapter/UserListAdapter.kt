package com.logicline.mydining.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.logicline.mydining.BuildConfig
import com.logicline.mydining.R
import com.logicline.mydining.databinding.LayoutUserItemBinding
import com.logicline.mydining.models.User
import com.logicline.mydining.ui.ProfileActivity
import com.logicline.mydining.utils.Constant


class UserListAdapter(val context: Context, val userList : MutableList<User>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {


    interface OnAction{
        fun onDeleteClick(userId: String)
    }


    var onAction : OnAction? = null

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
                    .load(BuildConfig.BASE_URL+url)
                    .into(binding.profileImage)
            }

            binding.txtName.text = user.name

            if(user.active=="1"){
                Glide.with(context)
                    .load(R.drawable.check)
                    .into(binding.active)
            }

            binding.txtUserRole.text = Constant.getUserType(user.accType)

            if (Constant.isSuperUser()){
                binding.layTrash.visibility = View.VISIBLE
                binding.layTrash.setOnClickListener {
                    onAction?.onDeleteClick(user.id!!)
                }
            }else{
                binding.layTrash.visibility = View.GONE

            }





            binding.root.setOnClickListener {
                context.startActivity(Intent(context, ProfileActivity::class.java).putExtra("profile", user))
            }
        }
    }
}