package com.logicline.mydining.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityIncomingJoinRequestsBinding
import com.logicline.mydining.data.models.response.IncomingJoinRequest
import com.logicline.mydining.ui.adapters.IncomingJoinRequestsAdapter
import com.logicline.mydining.ui.custom.GenericDialog
import com.logicline.mydining.ui.custom.StatusView
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class IncomingJoinRequestsActivity : BaseActivity() {
    private lateinit var binding: ActivityIncomingJoinRequestsBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var adapter: IncomingJoinRequestsAdapter
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomingJoinRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Incoming Join Requests"
        
        loadingDialog = LoadingDialog(this)
        setupRecyclerView()
        
        // Initially hide status view to show recycler view
        binding.statusView.hideStatusView()
        
        loadIncomingRequests()
    }

    private fun setupRecyclerView() {
        adapter = IncomingJoinRequestsAdapter(
            onAcceptClick = { request ->
                showAcceptDialog(request)
            },
            onRejectClick = { request ->
                showRejectDialog(request)
            }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadIncomingRequests() {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.getMessJoinRequests()
                loadingDialog.hide()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (!apiResponse?.error!!) {
                        val data = apiResponse.data
                        if (data != null) {
                            adapter.submitList(data.join_requests)
                            
                            if (data.join_requests.isEmpty()) {
                                binding.statusView.setStatus(StatusView.StatusType.EMPTY, "No incoming join requests")
                                    .setPositiveButton("Invite Members") {
                                        // Navigate to invite members or handle action
                                        shortToast("Invite members feature")
                                    }
                                    .showStatusView()
                            } else {
                                binding.statusView.hideStatusView()
                            }
                        }
                    } else {
                        // API returned an error
                        binding.statusView.setStatus(
                            StatusView.StatusType.ERROR,
                            apiResponse.message
                        )
                            .setPositiveButton("Retry") {
                                loadIncomingRequests()
                            }
                            .showStatusView()
                    }
                } else {
                    // HTTP error
                    binding.statusView.setStatus(
                        StatusView.StatusType.ERROR,
                        "Failed to load incoming requests: ${response.message()}"
                    )
                        .setPositiveButton("Retry") {
                            loadIncomingRequests()
                        }
                        .showStatusView()
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("IncomingJoinRequestsActivity", "Error loading incoming requests", e)
                binding.statusView.setStatus(StatusView.StatusType.ERROR, "Error: ${e.message}")
                    .setPositiveButton("Retry") {
                        loadIncomingRequests()
                    }
                    .showStatusView()            }
        }
    }

    private fun showAcceptDialog(request: IncomingJoinRequest) {
        GenericDialog.Builder(this)
            .setIcon(R.drawable.ic_check)
            .setTitle("Accept Join Request")
            .setContentView(R.layout.layout_accept_request_dialog)
            .setPositiveButton("Accept", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    val welcomeMessageEditText = genericDialog.findViewById<EditText>(R.id.et_welcome_message)
                    val welcomeMessage = welcomeMessageEditText?.text.toString().takeIf { it.isNotBlank() }
                    acceptJoinRequest(request.id, welcomeMessage)
                    genericDialog.dismiss()
                }
            })
            .setNegativeButton("Cancel")
            .show()
    }    private fun showRejectDialog(request: IncomingJoinRequest) {
        GenericDialog.Builder(this)
            .setIcon(R.drawable.ic_close)
            .setTitle("Reject Join Request")
            .setContentView(R.layout.layout_reject_request_dialog)
            .setPositiveButton("Reject", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    val reasonEditText = genericDialog.findViewById<EditText>(R.id.et_rejection_reason)
                    val reason = reasonEditText?.text.toString().takeIf { it.isNotBlank() }
                    rejectJoinRequest(request.id, reason)
                    genericDialog.dismiss()
                }
            })
            .setNegativeButton("Cancel")
            .show()
    }

    private fun acceptJoinRequest(requestId: Int, welcomeMessage: String?) {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.acceptJoinRequest(
                    requestId = requestId,
                    welcomeMessage = welcomeMessage
                )
                loadingDialog.hide()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (!apiResponse?.error!!) {
                        shortToast(apiResponse.message)
                        loadIncomingRequests() // Refresh the list
                    } else {
                        // API returned an error
                        shortToast(apiResponse.message)
                    }
                } else {
                    // HTTP error
                    shortToast("Failed to accept join request: ${response.message()}")
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("IncomingJoinRequestsActivity", "Error accepting join request", e)
                shortToast("Error: ${e.message}")
            }
        }
    }

    private fun rejectJoinRequest(requestId: Int, reason: String?) {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.rejectJoinRequest(
                    requestId = requestId,
                    reason = reason
                )
                loadingDialog.hide()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (!apiResponse?.error!!) {
                        shortToast(apiResponse.message)
                        loadIncomingRequests() // Refresh the list
                    } else {
                        // API returned an error
                        shortToast(apiResponse.message)
                    }
                } else {
                    // HTTP error
                    shortToast("Failed to reject join request: ${response.message()}")
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("IncomingJoinRequestsActivity", "Error rejecting join request", e)
                shortToast("Error: ${e.message}")
            }
        }
    }
}
