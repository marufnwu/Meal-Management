package com.logicline.mydining.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityAvailableMessesBinding
import com.logicline.mydining.data.models.response.AvailableMess
import com.logicline.mydining.data.models.response.AvailableMessesResponse
import com.logicline.mydining.ui.adapters.AvailableMessesAdapter
import com.logicline.mydining.ui.custom.GenericDialog
import com.logicline.mydining.ui.custom.StatusView
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AvailableMessesActivity : BaseActivity() {
    private lateinit var binding: ActivityAvailableMessesBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var adapter: AvailableMessesAdapter
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAvailableMessesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Available Messes"

        loadingDialog = LoadingDialog(this)
        setupRecyclerView()

        // Initially hide status view to show recycler view
        binding.statusView.hideStatusView()

        loadAvailableMesses()
    }

    private fun setupRecyclerView() {
        adapter = AvailableMessesAdapter { mess ->
            if (mess.join_request_exists) {
                shortToast("You already have a pending request for this mess")
            } else {
                showJoinRequestDialog(mess)
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadAvailableMesses() {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.getAvailableMesses()
                loadingDialog.hide()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (!apiResponse?.error!!) {
                        val data = apiResponse.data
                        if (data != null) {
                            adapter.submitList(data.messes)

                            if (data.messes.isEmpty()) {
                                binding.statusView.setStatus(
                                    StatusView.StatusType.EMPTY,
                                    "No available messes found"
                                )
                                    .setPositiveButton("Create New Mess") {
                                        // Navigate to create mess or handle action
                                        shortToast("Create new mess feature")
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
                                loadAvailableMesses()
                            }
                            .showStatusView()
                    }
                } else {
                    // HTTP error
                    binding.statusView.setStatus(
                        StatusView.StatusType.ERROR,
                        "Failed to load available messes: ${response.message()}"
                    )
                        .setPositiveButton("Retry") {
                            loadAvailableMesses()
                        }
                        .showStatusView()
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("AvailableMessesActivity", "Error loading available messes", e)
                binding.statusView.setStatus(
                    StatusView.StatusType.ERROR,
                    "Error: ${e.message}"
                )
                    .setPositiveButton("Retry") {
                        loadAvailableMesses()
                    }
                    .showStatusView()
            }
        }
    }

    private fun showJoinRequestDialog(mess: AvailableMess) {
        GenericDialog.Builder(this)
            .setIcon(R.drawable.ic_group)
            .setTitle("Join ${mess.mess.name}")
            .setContentView(R.layout.layout_join_request_dialog)
            .setPositiveButton("Send Request", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    val messageEditText = genericDialog.findViewById<EditText>(R.id.et_message)
                    val message = messageEditText?.text.toString().takeIf { it.isNotBlank() }
                    sendJoinRequest(mess.mess.id, message)
                    genericDialog.dismiss()
                }
            })
            .setNegativeButton("Cancel")
            .show()
    }

    private fun sendJoinRequest(messId: Int, message: String?) {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.sendJoinRequest(messId, message)
                loadingDialog.hide()

                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (!apiResponse?.error!!) {
                        shortToast(apiResponse.message)
                        // Refresh the list to update the join request status
                        loadAvailableMesses()
                    } else {
                        // API returned an error
                        shortToast(apiResponse.message)
                    }
                } else {
                    // HTTP error
                    shortToast("Failed to send join request: ${response.message()}")
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("AvailableMessesActivity", "Error sending join request", e)
                shortToast("Error: ${e.message}")
            }
        }
    }
}
