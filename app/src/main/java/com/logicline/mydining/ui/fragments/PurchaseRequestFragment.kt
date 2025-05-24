package com.logicline.mydining.ui.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.logicline.mydining.R
import com.logicline.mydining.ui.adapter.PurchaseRequestAdapter
import com.logicline.mydining.databinding.FragmentPurchaseRequestLayoutBinding
import com.logicline.mydining.data.models.PurchaseRequest
import com.logicline.mydining.data.models.response.GenericRespose
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.utils.Constant
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.MyApplication
import com.logicline.mydining.data.enums.PurchaseRequestStatus
import com.logicline.mydining.data.enums.PurchaseType
import com.logicline.mydining.data.models.response.PurchaseListResponse
import com.logicline.mydining.databinding.DialogPurchaseRequestDetailsBinding
import com.logicline.mydining.ui.adapter.PurchaseDetailAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class PurchaseRequestFragment : Fragment() {

    enum class Type {
        PENDING,
        ACCEPTED,
        REJECTED,
    }

    lateinit var recyView: RecyclerView
    lateinit var month: String
    lateinit var year: String
    private lateinit var status: PurchaseRequestStatus

    val requestItems: MutableList<PurchaseRequest> = mutableListOf()

    lateinit var adapter: PurchaseRequestAdapter
    lateinit var loadingDialog: LoadingDialog

    lateinit var layoutEmpty: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        loadingDialog = LoadingDialog(requireActivity())

        month = Constant.getCurrentMonthNumber()
        year = Constant.getCurrentYear()

        return FragmentPurchaseRequestLayoutBinding.inflate(inflater, container, false).root
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyView = view.findViewById(R.id.recyclerviewRequest)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)

        // Inside PurchaseRequestFragment, to get the status:
        val statusValue = arguments?.getInt("Type")
        status = statusValue?.let { PurchaseRequestStatus.fromValue(it) } ?: PurchaseRequestStatus.PENDING

        adapter = PurchaseRequestAdapter(requireContext(), requestItems, status)

        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL

        // Set click listener for showing dialog
        adapter.setOnItemClickListener { request ->
            showDetailsDialog(request)
        }

        // Set action listener for dialog actions
        adapter.setOnActionClickListener(object : PurchaseRequestAdapter.OnActionClick {
            override fun onAccept(requestId: Int, isDeposit: Int, purchaseType: Int, position: Int) {
                acceptRequest(requestId, isDeposit, purchaseType, position)
            }

            override fun onReject(requestId: Int, position: Int) {
                rejectRequest(requestId, position)
            }
        })

        recyView.layoutManager = llm
        recyView.setHasFixedSize(true)
        recyView.adapter = adapter

        getRequestList()
    }

    private fun showDetailsDialog(request: PurchaseRequest) {
        val dialog = PurchaseRequestDetailsDialog.newInstance(request)

        dialog.setOnRequestActionListener(object : PurchaseRequestDetailsDialog.OnRequestActionListener {
            override fun onAcceptRequest(requestId: Int, isDeposit: Int, purchaseType: Int) {
                val position = requestItems.indexOfFirst { it.id == requestId }
                if (position != -1) {
                    acceptRequest(requestId, isDeposit, purchaseType, position)
                }
            }

            override fun onRejectRequest(requestId: Int) {
                val position = requestItems.indexOfFirst { it.id == requestId }
                if (position != -1) {
                    rejectRequest(requestId, position)
                }
            }
        })

        dialog.show(childFragmentManager, "request_details")
    }

    private fun rejectRequest(requestId: Int, position: Int) {
        loadingDialog.show()
        (activity?.applicationContext as MyApplication)
            .myApi.rejectPurchaseRequest(requestId)
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(call: Call<GenericRespose>, response: Response<GenericRespose>) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null) {
                        if(!response.body()!!.error) {
                            if(requestItems.size > position) {
                                requestItems.removeAt(position)
                                adapter.notifyItemRemoved(position)
                                checkListEmpty()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    loadingDialog.hide()
                }
            })
    }

    private fun getRequestList() {
        loadingDialog.show()
        (requireActivity().applicationContext as MyApplication)
            .myApi
            .getPurchaseRequests(status = status.value)
            .enqueue(object : Callback<ServerResponse<PurchaseListResponse<PurchaseRequest>>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<ServerResponse<PurchaseListResponse<PurchaseRequest>>>,
                    response: Response<ServerResponse<PurchaseListResponse<PurchaseRequest>>>
                ) {
                    loadingDialog.hide()
                    if(response.isSuccessful && response.body()!=null) {
                        if(!response.body()!!.error) {
                            requestItems.clear()
                            response.body()!!.data?.let {
                                if(it.purchases.isNotEmpty()) {
                                    requestItems.addAll(it.purchases)
                                }
                                checkListEmpty()
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ServerResponse<PurchaseListResponse<PurchaseRequest>>>, t: Throwable) {
                    loadingDialog.hide()
                }
            })
    }

    private fun checkListEmpty() {
        if (requestItems.isEmpty()) {
            recyView.visibility = View.GONE
            layoutEmpty.visibility = View.VISIBLE
        } else {
            recyView.visibility = View.VISIBLE
            layoutEmpty.visibility = View.GONE
        }
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
                    if(response.isSuccessful && response.body()!=null) {
                        if(!response.body()!!.error) {
                            if(requestItems.size > position) {
                                requestItems.removeAt(position)
                                adapter.notifyItemRemoved(position)
                                checkListEmpty()
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
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), {
                view, year, monthOfYear, dayOfMonth ->
            val date = year.toString()+"-"+(monthOfYear+1)+"-"+dayOfMonth

            this.year = Constant.getYear(date)
            this.month = Constant.getMonthNumber(date)

            getRequestList()
        }, mYear, mMonth, mDay)

        datePickerDialog.show()
    }

    companion object {
        fun create(type: PurchaseRequestStatus): PurchaseRequestFragment {
            val fragment = PurchaseRequestFragment()
            val bundle = Bundle()
            bundle.putInt("Type", type.value)
            fragment.arguments = bundle
            return fragment
        }
    }
}

class PurchaseRequestDetailsDialog : DialogFragment() {

    private var _binding: DialogPurchaseRequestDetailsBinding? = null
    private val binding get() = _binding!!

    private var purchaseRequest: PurchaseRequest? = null
    private var productAdapter: PurchaseDetailAdapter? = null

    private var onActionListener: OnRequestActionListener? = null

    interface OnRequestActionListener {
        fun onAcceptRequest(requestId: Int, isDeposit: Int, purchaseType: Int)
        fun onRejectRequest(requestId: Int)
    }

    companion object {
        private const val ARG_REQUEST = "purchase_request"

        fun newInstance(request: PurchaseRequest): PurchaseRequestDetailsDialog {
            val fragment = PurchaseRequestDetailsDialog()
            val args = Bundle()
            args.putParcelable(ARG_REQUEST, request)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        purchaseRequest = arguments?.getParcelable(ARG_REQUEST)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogPurchaseRequestDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Set the width to match parent (full width)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // Optional: Set window animations
            attributes?.windowAnimations = R.style.DialogAnimation
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        bindData()
        setupListeners()
    }

    private fun setupRecyclerView() {
        productAdapter = PurchaseDetailAdapter()
        binding.rvDialogProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
            setHasFixedSize(true)
        }
    }

    private fun bindData() {
        purchaseRequest?.let { request ->
            // Set header info
            binding.txtDialogName.text = request.name
            binding.txtDialogDate.text = request.date
            binding.txtDialogAmount.text = String.format("$%.2f", request.price)

            // Set product list
            request.productJson?.let { products ->
                productAdapter?.submitList(products)
                binding.rvDialogProducts.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
                binding.labelProducts.visibility = if (products.isEmpty()) View.GONE else View.VISIBLE
            } ?: run {
                binding.rvDialogProducts.visibility = View.GONE
                binding.labelProducts.visibility = View.GONE
            }

            // Set deposit info
            if (request.depositRequest) {
                binding.txtDialogIsDeposit.visibility = View.VISIBLE
                binding.txtDialogIsDeposit.text = "${request.name} requested to deposit money to their account"
                binding.checkBoxDialogDeposit.isChecked = true
            } else {
                binding.txtDialogIsDeposit.visibility = View.GONE
                binding.checkBoxDialogDeposit.isChecked = false
            }

            // Set purchase type
            if (request.purchaseType == PurchaseType.MEAL.value) {
                binding.rGroupDialogPurchaseType.check(R.id.rButtonDialogMealPurchase)
            } else {
                binding.rGroupDialogPurchaseType.check(R.id.rButtonDialogOtherPurchase)
            }

            // Set action buttons visibility based on permissions and status
            val isManagerOrAdmin = Constant.isManagerOrSuperUser()
            binding.btnDialogReject.visibility = if (isManagerOrAdmin && request.status == 0) View.VISIBLE else View.GONE
            binding.btnDialogAccept.visibility = if (isManagerOrAdmin && request.status == 0) View.VISIBLE else View.GONE

            binding.checkBoxDialogDeposit.isEnabled = isManagerOrAdmin && request.status == 0
            binding.rGroupDialogPurchaseType.isEnabled = isManagerOrAdmin && request.status == 0
        }
    }

    private fun setupListeners() {
        binding.btnDialogCancel.setOnClickListener {
            dismiss()
        }

        binding.btnDialogAccept.setOnClickListener {
            purchaseRequest?.let { request ->
                val isDeposit = if (binding.checkBoxDialogDeposit.isChecked) 1 else 0
                val purchaseType = if (binding.rGroupDialogPurchaseType.checkedRadioButtonId == R.id.rButtonDialogMealPurchase) 1 else 2
                onActionListener?.onAcceptRequest(request.id, isDeposit, purchaseType)
                dismiss()
            }
        }

        binding.btnDialogReject.setOnClickListener {
            purchaseRequest?.let { request ->
                onActionListener?.onRejectRequest(request.id)
                dismiss()
            }
        }
    }

    fun setOnRequestActionListener(listener: OnRequestActionListener) {
        this.onActionListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}