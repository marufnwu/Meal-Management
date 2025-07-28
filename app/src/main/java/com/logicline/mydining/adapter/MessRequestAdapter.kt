package com.logicline.mydining.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.databinding.LayoutMessJoinRequestBinding
import com.logicline.mydining.models.MessRequest
import com.logicline.mydining.ui.SwitchMessActivity
import com.logicline.mydining.utils.JDialog
import com.logicline.mydining.utils.MyExtensions.shortToast

class MessRequestAdapter(val context: Context, val list:MutableList<MessRequest>) : RecyclerView.Adapter<MessRequestAdapter.MyViewHolder>() {

    var onCancel: ((reqId:Int)->Unit)? = null
    var onAccept: ((reqId:Int)->Unit)? = null

    inner class MyViewHolder(val binding : LayoutMessJoinRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(messRequest: MessRequest){
            binding.txtMessName.text  = messRequest.mess_name
            binding.txtDate.text  = messRequest.request_date


            when(messRequest.status){
                0->binding.imgStatus.setImageResource(R.drawable.time)
                1->binding.imgStatus.setImageResource(R.drawable.tick)
                2->binding.imgStatus.setImageResource(R.drawable.cross)
                3->binding.imgStatus.setImageResource(R.drawable.cross)
            }

            binding.root.setOnClickListener {
                if(messRequest.status==1){
                    context.shortToast("Request Accepted by mess")
                    return@setOnClickListener
                }

                if(messRequest.status==2){
                    context.shortToast("Request canceled by yourself")
                    return@setOnClickListener
                }

                if(messRequest.status==3){
                    context.shortToast("Request rejected by mess manager")
                    return@setOnClickListener
                }

                JDialog.make(context)
                    .setBodyText("Your mess switch request is now under pending. You can cancel mess switch request.")
                    .setCancelable(true)
                    .setPositiveButton("Cancel Request"){
                        it.hideDialog()
                        onCancel?.invoke(messRequest.id)
                    }.setNegativeButton("Dismiss"){
                        it.hideDialog()
                    }

                    .build()
                    .showDialog()
            }
        }

        fun bindMemberRequest(messRequest: MessRequest){
            binding.txtMessName.text  = messRequest.name
            binding.txtDate.text  = messRequest.request_date


            when(messRequest.status){
                0->binding.imgStatus.setImageResource(R.drawable.time)
                1->binding.imgStatus.setImageResource(R.drawable.tick)
                2->binding.imgStatus.setImageResource(R.drawable.cross)
                3->binding.imgStatus.setImageResource(R.drawable.cross)
            }

            binding.root.setOnClickListener {
                if(messRequest.status==1){
                    context.shortToast("Request Accepted by mess")
                    return@setOnClickListener
                }

                if(messRequest.status==2){
                    context.shortToast("Request canceled by user")
                    return@setOnClickListener
                }

                if(messRequest.status==3){
                    context.shortToast("Request rejected by mess manager")
                    return@setOnClickListener
                }

                JDialog.make(context)
                    .setBodyText("${messRequest.name} is requested to join your mess. You can accept him/her join request by clicking accept button or reject request by clicking reject button")
                    .setCancelable(true)
                    .setPositiveButton("Accept"){
                        it.hideDialog()
                        onAccept?.invoke(messRequest.id)
                    }.setNegativeButton("Reject"){
                        it.hideDialog()
                        onCancel?.invoke(messRequest.id)
                    }

                    .build()
                    .showDialog()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutMessJoinRequestBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if(context is SwitchMessActivity){
            holder.bind(list[position])
        }else{
            holder.bindMemberRequest(list[position])
        }
    }
}