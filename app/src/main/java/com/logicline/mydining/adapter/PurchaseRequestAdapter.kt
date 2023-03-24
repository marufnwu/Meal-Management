package com.logicline.mydining.adapter

import android.R.array
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import androidx.core.text.htmlEncode
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.logicline.mydining.R
import com.logicline.mydining.databinding.LayoutPurchaseRequestItemBinding
import com.logicline.mydining.models.PurchaseProduct
import com.logicline.mydining.models.PurchaseRequest
import com.logicline.mydining.ui.fragments.PurchaseRequestFragment
import com.logicline.mydining.utils.Constant
import java.lang.reflect.Type

//Type 0, pending
//Type 1, Accepted
//Type 2, Rejected



class PurchaseRequestAdapter(val context:Context, val items : MutableList<PurchaseRequest>, val type:PurchaseRequestFragment.Type ) : RecyclerView.Adapter<PurchaseRequestAdapter.MyViewHolder>() {
    var selectedPos = -1
    private var onActionClick : OnActionClick? = null

    interface OnActionClick{
        fun OnAccept(requestId:Int, isDeposit:Int, purchaseType:Int, position: Int)
        fun OnReject(requestId:Int, position: Int)
    }

    fun setAction(onActionClick: OnActionClick){
        this.onActionClick = onActionClick
    }

    inner class MyViewHolder(val binding : LayoutPurchaseRequestItemBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(purchaseRequest: PurchaseRequest){
            binding.txtDate.text = purchaseRequest.date
            binding.txtName.text = purchaseRequest.name
            binding.txtTotalPrice.text = purchaseRequest.price.toString()

            if(!Constant.isManagerOrSuperUser()){
                binding.layoutAction.visibility = View.GONE
                binding.checkBoxDeposit.isEnabled = false
                binding.rGroupPurchaseType.isEnabled = false
            }else{
                if(type!=PurchaseRequestFragment.Type.PENDING){
                    binding.layoutAction.visibility = View.GONE
                    binding.checkBoxDeposit.isEnabled = false
                    binding.rGroupPurchaseType.isEnabled = false
                }
                binding.btnAccept.setOnClickListener {
                    val isDeposit = if(binding.checkBoxDeposit.isChecked) 1 else 0
                    val purchaseType = if(binding.rGroupPurchaseType.checkedRadioButtonId==R.id.rButtonMealPurchase){
                        1
                    }else{
                        2
                    }

                    onActionClick?.let {
                        it.OnAccept(purchaseRequest.id, isDeposit, purchaseType, adapterPosition)
                    }


                }

                binding.btnReject.setOnClickListener {
                    onActionClick?.let {
                        it.OnReject(purchaseRequest.id, adapterPosition)
                    }
                }

            }




            if(purchaseRequest.depositRequest==1){
                binding.txtIsDeposit.visibility = View.VISIBLE
                binding.txtIsDeposit.text = "${purchaseRequest.name} request to deposit"


                binding.checkBoxDeposit.isChecked = true

            }else{
                binding.txtIsDeposit.visibility = View.GONE
                binding.checkBoxDeposit.isChecked = false

            }

            if(purchaseRequest.purchase_type==1){
                //meal purchase
                binding.rGroupPurchaseType.check(R.id.rButtonMealPurchase)
            }else{
                //other purchase
                binding.rGroupPurchaseType.check(R.id.rButtonOtherPurchase)
            }

            purchaseRequest.type?.let {
                if(it.equals("list")){
                    purchaseRequest.productJson?.let {
                        val jsonParser = JsonParser()
                        val jsonArray = jsonParser.parse(it).asJsonArray
                        val type: Type = object : TypeToken<List<PurchaseProduct?>?>() {}.type
                        val lista: List<PurchaseProduct> = Gson().fromJson(jsonArray, type)
                        val html = generateHtml(lista);

                        Log.d("sdssdsd",html)
                        binding.webProductList.loadDataWithBaseURL(null, html, "text/html; charset=utf-8", "UTF-8", null)

                    }
                }else{

                }
            }


            binding.imgArrow.setOnClickListener {
                if(selectedPos==adapterPosition){
                    //deselect
                    selectedPos = -1
                    val slideUp: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_right)
                    slideUp.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {
                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            binding.layoutInfo.visibility  =View.GONE
                        }

                        override fun onAnimationRepeat(p0: Animation?) {
                        }

                    })
                    binding.layoutInfo.startAnimation(slideUp)


                }else{
                    //select
                    selectedPos = adapterPosition
                    notifyDataSetChanged()
                }
            }

            if(selectedPos==adapterPosition){

                //expand(binding.layoutInfo)
                //binding.layoutInfo.animate().alpha(1.0f)
                binding.layoutInfo.visibility  = View.VISIBLE
                val slideUp: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_in_left)
                binding.layoutInfo.startAnimation(slideUp)

            }else{
                //collapse(binding.layoutInfo)
                val slideUp: Animation = AnimationUtils.loadAnimation(context, R.anim.anim_slide_out_left)
                binding.layoutInfo.startAnimation(slideUp)
                binding.layoutInfo.visibility  = View.GONE

            }





        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutPurchaseRequestItemBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    private fun generateHtml(items : List<PurchaseProduct>) :String{

        var body = "";

        items.forEach {
            val htmlItem = """ <div style="background: #F5F5DC; width: 100%; margin: 0 auto;  color: black;">
            <div style="padding: 8px; border-bottom: solid 2px #000000; display: flex; text-align:center">
    
            <div style="width: 50%; ">
            ${it.name}
            </div>
    
            <div style="width: 50%;">
            ${it.price}
            </div>
    
            </div>
            </div>""".trimIndent()

            body += htmlItem
        }


        return "<div>${body}</div>";

    }
}