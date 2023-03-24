package com.logicline.mydining.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.adapter.PurchaseRequestAdapter
import com.logicline.mydining.databinding.FragmentPurchaseRequestLayoutBinding
import com.logicline.mydining.models.PurchaseRequest
import com.logicline.mydining.models.response.GenericRespose
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class PurchaseRequestFragment : Fragment() {


    enum class Type{
        PENDING,
        ACCEPTED,
        REJECTED,
    }

    lateinit var recyView: RecyclerView
    lateinit var txtMonthYear: TextView
    lateinit var month : String
    lateinit var year : String

    var type:Int = -1

    val requestItems: MutableList<PurchaseRequest> = mutableListOf()

    lateinit var adapter : PurchaseRequestAdapter
    lateinit var loadingDialog: LoadingDialog

    lateinit var layoutEmpty : LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loadingDialog = LoadingDialog(requireActivity())

        month = Constant.getCurrentMonthNumber()
        year = Constant.getCurrentYear()

        return FragmentPurchaseRequestLayoutBinding.inflate(inflater, container, false).root
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyView = view.findViewById<RecyclerView>(R.id.recyclerviewRequest)
        txtMonthYear = view.findViewById(R.id.txtMonthYear)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)


        txtMonthYear.setOnClickListener {
            showDateTimePicker()
        }


        type = arguments?.getInt("Type")!!

        val typeN = if(type==0){
            Type.PENDING
        }else if(type==1){
            Type.ACCEPTED
        } else {
            Type.REJECTED
        }


        adapter = PurchaseRequestAdapter(requireContext(), requestItems, typeN)




        txtMonthYear.text = Constant.getCurrentMonthName()+" "+ Constant.getCurrentYear()


        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL

        adapter.setAction(object : PurchaseRequestAdapter.OnActionClick {
            override fun OnAccept(requestId: Int, isDeposit: Int, purchaseType: Int, position:Int) {
                acceptRequest(requestId, isDeposit, purchaseType, position)
            }

            override fun OnReject(requestId: Int, position: Int) {
                rejectRequest(requestId, position)
            }

        })

        recyView.layoutManager = llm
        recyView.setHasFixedSize(true)
        recyView.adapter = adapter

        getRequestList()

    }

    private fun rejectRequest(requestId: Int, position: Int) {
        loadingDialog.show()
        (activity?.applicationContext as MyApplication)
            .myApi.rejectPurchaseRequest(requestId)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            if(requestItems.size>=position){
                                requestItems.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun getRequestList(){
        loadingDialog.show()
        (requireActivity().applicationContext as MyApplication)
            .myApi
            .getPurchaseRequestManager(year, month, type)
            .enqueue(object : Callback<ServerResponse<List<PurchaseRequest>>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ServerResponse<List<PurchaseRequest>>>, response: Response<ServerResponse<List<PurchaseRequest>>>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            requestItems.clear()
                            response.body()!!.data?.let {

                                if(it.size>0){
                                    recyView.visibility = View.VISIBLE
                                    layoutEmpty.visibility = View.GONE
                                    requestItems.addAll(it)
                                    adapter.notifyDataSetChanged()
                                }else{
                                    recyView.visibility = View.GONE
                                    layoutEmpty.visibility = View.VISIBLE
                                }


                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<List<PurchaseRequest>>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    private fun acceptRequest(requestId: Int, deposit: Int, purchaseType: Int, position: Int) {
        loadingDialog.show()
        (activity?.applicationContext as MyApplication)
            .myApi.acceptPurchaseRequest(requestId, deposit, purchaseType)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            if(requestItems.size>=position){
                                requestItems.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }



    @SuppressLint("SetTextI18n")
    public fun showDateTimePicker() {
        // Get Current Date
        // Get Current Date
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            this.year = Constant.getYear(date)
            this.month = Constant.getMonthNumber(date)

            txtMonthYear.text = Constant.getMonthName(date)+" "+Constant.getYear(date)

            getRequestList()

        }, mYear, mMonth, mDay)

        datePickerDialog.show()

    }


    companion object {
        fun create(type: Int) : PurchaseRequestFragment {
            val fragment = PurchaseRequestFragment()
            val bundle = Bundle()
            bundle.putInt("Type", type)
            fragment.arguments = bundle
            return fragment
        }
    }
}