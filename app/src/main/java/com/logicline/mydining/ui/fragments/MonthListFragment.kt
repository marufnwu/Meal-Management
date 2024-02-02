package com.logicline.mydining.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.adapter.MonthAdapter
import com.logicline.mydining.databinding.FragmentMonthListBinding
import com.logicline.mydining.models.Month
import com.logicline.mydining.models.MonthOfYear
import com.logicline.mydining.models.response.ServerResponse
import com.logicline.mydining.ui.PreviousMonthActivity
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.MyApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class MONTH_TYPE{
    MANUAL,
    AUTO
}

private const val ARG_MONTH_TYPE = "param1"

class MonthListFragment :  Fragment(){
    lateinit var  loadingDialog: LoadingDialog
    private val months: MutableList<Any> = mutableListOf()
    lateinit var adapter: MonthAdapter
    lateinit var binding: FragmentMonthListBinding

    lateinit var monthType: MONTH_TYPE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            it.getString(ARG_MONTH_TYPE)?.let {
                monthType = MONTH_TYPE.valueOf(it)
            }

        }
        loadingDialog = LoadingDialog(requireActivity())
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMonthListBinding.inflate(inflater, container, false);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        if(monthType==MONTH_TYPE.MANUAL){
            getMonthList()
        }else{
            getDateMonthList()
        }
    }

    private fun initViews() {
        adapter = MonthAdapter(requireContext(), months)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter

        adapter.onAutoMonthClick = {
            startActivity(
                Intent(requireContext(), PreviousMonthActivity::class.java)
                .putExtra(Constant.YEAR, it.year)
                .putExtra(Constant.MONTH, it.month.toString()))
        }

        adapter.onManualMonthClick = {
            startActivity(
                Intent(requireContext(), PreviousMonthActivity::class.java)
                    .putExtra(Constant.MONTH_NAME, it.name)
                    .putExtra(Constant.MONTH_ID, it.id.toString()))
        }
    }

    private fun getDateMonthList() {
        loadingDialog.show()
        (requireContext().applicationContext as MyApplication)
            .myApi
            .getActiveMonthList()
            .enqueue(object : Callback<ServerResponse<MutableList<MonthOfYear>>> {
                override fun onResponse(
                    call: Call<ServerResponse<MutableList<MonthOfYear>>>,
                    response: Response<ServerResponse<MutableList<MonthOfYear>>>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            months.addAll(response.body()!!.data!!)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(
                    call: Call<ServerResponse<MutableList<MonthOfYear>>>,
                    t: Throwable
                ) {
                    loadingDialog.hide()
                }

            })
    }
    private fun getMonthList() {
        loadingDialog.show()

        (requireContext().applicationContext as MyApplication)
            .myApi
            .getAllMonthList()
            .enqueue(object : Callback<ServerResponse<List<Month>>> {
                override fun onResponse(
                    call: Call<ServerResponse<List<Month>>>,
                    response: Response<ServerResponse<List<Month>>>
                ) {
                    loadingDialog.hide()
                    if (response.isSuccessful && response.body()!=null){
                        if(!response.body()!!.error){
                            months.addAll(response.body()!!.data!!)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<List<Month>>>, t: Throwable) {
                    loadingDialog.hide()
                }

            })
    }

    companion object{
        fun newInstance(type: MONTH_TYPE) = MonthListFragment().apply {
            arguments = bundleOf(
                ARG_MONTH_TYPE to type.toString()
            )
        }

    }


}