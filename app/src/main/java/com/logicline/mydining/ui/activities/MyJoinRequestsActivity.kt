package com.logicline.mydining.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.databinding.ActivityMyJoinRequestsBinding
import com.logicline.mydining.data.models.response.UserJoinRequest
import com.logicline.mydining.ui.adapters.MyJoinRequestsAdapter
import com.logicline.mydining.ui.custom.GenericDialog
import com.logicline.mydining.ui.custom.StatusView
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyJoinRequestsActivity : BaseActivity() {
    private lateinit var binding: ActivityMyJoinRequestsBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var adapter: MyJoinRequestsAdapter
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyJoinRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Join Requests"
        
        loadingDialog = LoadingDialog(this)
        setupRecyclerView()
        
        // Initially hide status view to show recycler view
        binding.statusView.hideStatusView()
        
        loadJoinRequests()
    }

    private fun setupRecyclerView() {
        adapter = MyJoinRequestsAdapter(
            onCancelClick = { request ->
                showCancelConfirmation(request)
            }
        )
        
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadJoinRequests() {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.getUserJoinRequests()
                loadingDialog.hide()
                
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (!apiResponse?.error!!) {
                        val data = apiResponse.data
                        if (data != null) {
                            adapter.submitList(data.join_requests)
                            
                            if (data.join_requests.isEmpty()) {
                                binding.statusView.setStatus(StatusView.StatusType.EMPTY, "No join requests found")
                                    .setPositiveButton("Browse Available Messes") {
                                        // Navigate to available messes
                                        finish()
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
                                loadJoinRequests()
                            }
                            .showStatusView()
                    }
                } else {
                    // HTTP error
                    binding.statusView.setStatus(
                        StatusView.StatusType.ERROR, 
                        "Failed to load join requests: ${response.message()}"
                    )
                        .setPositiveButton("Retry") {
                            loadJoinRequests()
                        }
                        .showStatusView()
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("MyJoinRequestsActivity", "Error loading join requests", e)
                binding.statusView.setStatus(StatusView.StatusType.ERROR, "Error: ${e.message}")
                    .setPositiveButton("Retry") {
                        loadJoinRequests()
                    }
                    .showStatusView()
            }
        }
    }    private fun showCancelConfirmation(request: UserJoinRequest) {
        GenericDialog.Builder(this)
            .setTitle("Cancel Join Request")
            .setBodyText("Are you sure you want to cancel your join request to ${request.mess.name}?")
            .setPositiveButton("Cancel Request", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    cancelJoinRequest(request.id)
                }
            })
            .setNegativeButton("Keep Request")
            .show()
    }

    private fun cancelJoinRequest(requestId: Int) {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.cancelJoinRequest(requestId)
                loadingDialog.hide()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (!apiResponse?.error!!) {
                        shortToast(apiResponse.message)
                        loadJoinRequests() // Refresh the list
                    } else {
                        // API returned an error
                        shortToast(apiResponse.message)
                    }
                } else {
                    // HTTP error
                    shortToast("Failed to cancel join request: ${response.message()}")
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("MyJoinRequestsActivity", "Error cancelling join request", e)
                shortToast("Error: ${e.message}")
            }
        }
    }
}
