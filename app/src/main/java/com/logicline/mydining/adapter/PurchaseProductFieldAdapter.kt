package com.logicline.mydining.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.logicline.mydining.databinding.LayoutProductFieldItemBinding
import com.logicline.mydining.models.PurchaseProduct
import com.logicline.mydining.utils.MyExtensions.shortToast

class PurchaseProductFieldAdapter(val context: Context, val purchaseProducts : MutableList<PurchaseProduct>) : RecyclerView.Adapter<PurchaseProductFieldAdapter.MyViewHolder>() {


    interface FieldChange{
        fun onNameChange(name:String, position: Int)
        fun onPriceChange(price:Float, position: Int)
    }

    public var fieldChange : FieldChange? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutProductFieldItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(purchaseProducts.get(position), position)
    }

    override fun getItemCount(): Int {
        return purchaseProducts.size
    }

    private fun addBlankField(){
        purchaseProducts.add(PurchaseProduct())
        notifyDataSetChanged()
    }


    inner class MyViewHolder(val binding: LayoutProductFieldItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(purchaseProduct: PurchaseProduct, position: Int){

            Log.d("fff", Gson().toJson(purchaseProduct))

            if(!purchaseProduct.name.isNullOrEmpty()){
                binding.txtName.setText(purchaseProduct.name)
            }

            if(purchaseProduct.price!=null){
                binding.txtPrice.setText(purchaseProduct.price.toString())
            }

            binding.txtSl.setText((adapterPosition+1).toString())

        }
    }
}