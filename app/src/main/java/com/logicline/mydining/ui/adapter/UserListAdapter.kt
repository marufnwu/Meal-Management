package com.logicline.mydining.ui.adapter

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
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.data.enums.MessUserStatus
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.ui.activities.ProfileActivity
import com.logicline.mydining.utils.LocalDB


class UserListAdapter(val context: Context, val userList: MutableList<MessUser>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {


    interface OnAction{
        fun onDeleteClick(messUser: MessUser)
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
        fun bind(messUser: MessUser){
            messUser.user?.photoUrl?.let { url->
                Glide.with(context)
                    .load(BuildConfig.BASE_URL+url)
                    .into(binding.profileImage)
            }

            binding.txtName.text = messUser.user?.name

            if(messUser.status==MessUserStatus.ACTIVE.value){
                Glide.with(context)
                    .load(R.drawable.check)
                    .into(binding.active)
            }

            binding.txtUserRole.text =messUser.role?.role?: "User"

            if (LocalDB.getUserData()?.messUser?.hasAnyPermission(MessPermission.USER_MANAGEMENT) == true){
                binding.layTrash.visibility = View.VISIBLE
                binding.layTrash.setOnClickListener {
                    onAction?.onDeleteClick(messUser)
                }
            }else{
                binding.layTrash.visibility = View.GONE

            }





            binding.root.setOnClickListener {
                context.startActivity(Intent(context, ProfileActivity::class.java).putExtra("profile", messUser))
            }
        }
    }
}