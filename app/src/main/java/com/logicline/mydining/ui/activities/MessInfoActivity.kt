package com.logicline.mydining.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.logicline.mydining.R
import com.logicline.mydining.databinding.ActivityMessInfoBinding
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.data.enums.MessStatus
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.response.MessInfoResponse
import com.logicline.mydining.data.models.response.ServerResponse
import com.logicline.mydining.ui.custom.GenericDialog
import com.logicline.mydining.ui.custom.StatusView
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.AppPrefs
import com.logicline.mydining.utils.BaseActivity
import com.logicline.mydining.utils.Ext.MyExtensions.collectState
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.LoadingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityMessInfoBinding
    private lateinit var loadingDialog: LoadingDialog
    private var messCreateDialog: GenericDialog? = null
    private var currentMessInfo: MessInfoResponse? = null

    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.mess_info)
        binding = ActivityMessInfoBinding.inflate(layoutInflater)

        setContentView(binding.root)
        loadingDialog = LoadingDialog(this)

        initViews()
        setCollectors()
        loadMessInfo()
    }

    private fun setCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.messUserState.collectState(
                    lifecycleOwner = this@MessInfoActivity,
                    onLoading = { loadingDialog.show() },
                    onSuccess = {
                        loadingDialog.hide()
                        messCreateDialog?.dismiss()
                        setData(it?.mess)
                        loadMessInfo() // Reload detailed mess info
                    },
                    onError = {
                        loadingDialog.hide()
                        shortToast(it)
                    }
                )
            }
        }
    }

    private fun initViews() {
        binding.statusView.setPositiveButton(
            text = "Create Mess",
            isVisible = true
        ) {
            showCreateMessDialog()
        }

        binding.statusView.setNegativeButton(
            text = "Join Existing Mess",
            isVisible = true
        ) {
            openAvailableMesses()
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.syncCurrentMessUser()
            loadMessInfo()
        }

        // Mess Management Actions
        binding.btnLeaveMess.setOnClickListener {
            showLeaveMessConfirmation()
        }

        binding.btnCloseMess.setOnClickListener {
            showCloseMessConfirmation()
        }

        binding.btnJoinRequests.setOnClickListener {
            openIncomingJoinRequests()
        }

        binding.btnViewAvailableMesses.setOnClickListener {
            openAvailableMesses()
        }

        binding.btnMyJoinRequests.setOnClickListener {
            openMyJoinRequests()
        }
    }

    private fun loadMessInfo() {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.getCurrentMessInfo()
                loadingDialog.hide()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    currentMessInfo = response.body()?.data
                    setDetailedData(currentMessInfo)
                } else {
                    // If detailed mess info fails, just show basic mess data
                    val messUser = AppPrefs.messUser
                    if (messUser?.mess != null) {
                        setData(messUser.mess)
                    } else {
                        showNoMessState()
                    }
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("MessInfoActivity", "Error loading mess info", e)
                // Fallback to basic mess data
                val messUser = AppPrefs.messUser
                if (messUser?.mess != null) {
                    setData(messUser.mess)
                } else {
                    showNoMessState()
                }
            }
        }
    }

    private fun setDetailedData(messInfo: MessInfoResponse?) {
        if (messInfo != null) {
            binding.txtMessName.text = messInfo.mess.name
            binding.txtMessId.text = messInfo.mess.id.toString()
            binding.txtMessCreated.text = messInfo.mess.createdAt.toString()
            binding.txtStatus.text = MessStatus.fromValue(messInfo.mess.status)?.value ?: "Active"
            
            binding.statusView.hideStatusView()
            binding.layoutMessActions.visibility = android.view.View.VISIBLE
            
            setupPermissionBasedUI(messInfo.user_role.permissions)
        } else {
            showNoMessState()
        }
    }

    private fun setData(data: Mess?) {
        if (data != null) {
            binding.txtMessName.text = data.name
            binding.txtMessId.text = data.id.toString()
            binding.txtMessCreated.text = data.createdAt.toString()
            binding.txtStatus.text = MessStatus.fromValue(data.status)?.value ?: "Active"
            
            binding.statusView.hideStatusView()
            binding.layoutMessActions.visibility = android.view.View.VISIBLE
            
            // Use current user permissions for basic setup
            val messUser = AppPrefs.messUser
            val permissions = messUser?.role?.permissions?.map { it.permission } ?: emptyList()
            setupPermissionBasedUI(permissions)
        } else {
            showNoMessState()
        }
    }

    private fun showNoMessState() {
        binding.statusView.setStatus(
            StatusView.StatusType.EMPTY,
            "No Mess Found! Please create a mess or join existing mess."
        )
        binding.statusView.showStatusView()
        binding.layoutMessActions.visibility = android.view.View.GONE
    }

    private fun setupPermissionBasedUI(permissions: List<String>) {
        val messUser = AppPrefs.messUser
        
        // Show/hide Close Mess button based on permission
        if (messUser.hasAnyPermission(MessPermission.MESS_CLOSE)) {
            binding.btnCloseMess.visibility = android.view.View.VISIBLE
        }

        // Show/hide Manage Join Requests button based on permission
        if (messUser.hasAnyPermission(MessPermission.JOIN_REQUEST_MANAGEMENT)) {
            binding.btnJoinRequests.visibility = android.view.View.VISIBLE
        }
    }

    private fun createMess(name: String) {
        viewModel.createMess(name)
    }    private fun showCreateMessDialog() {
        messCreateDialog = GenericDialog.Builder(this)
            .setIcon(R.drawable.add)
            .setTitle("Create Mess!")
            .setPositiveButton("Create Mess", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    val name = genericDialog.findViewById<EditText>(R.id.ev_mess_name)
                    createMess(name?.text?.toString() ?: "")
                }
            })
            .setNegativeButton("Cancel")
            .setAutoDismiss(false)
            .setContentView(R.layout.layout_create_mess)
            .show()
    }    private fun showLeaveMessConfirmation() {
        GenericDialog.Builder(this)
            .setIcon(R.drawable.ic_warning)
            .setTitle("Leave Mess")
            .setBodyText("Are you sure you want to leave this mess? This action cannot be undone.")
            .setPositiveButton("Leave", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    leaveMess()
                }
            })
            .setNegativeButton("Cancel")
            .show()
    }

    private fun showCloseMessConfirmation() {
        GenericDialog.Builder(this)
            .setIcon(R.drawable.ic_warning)
            .setTitle("Close Mess")
            .setBodyText("Are you sure you want to close this mess permanently? This will affect all members and cannot be undone.")
            .setPositiveButton("Close Mess", object : GenericDialog.OnClickListener {
                override fun onClick(genericDialog: GenericDialog) {
                    closeMess()
                }
            })
            .setNegativeButton("Cancel")
            .show()
    }

    private fun leaveMess() {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.leaveMess()
                loadingDialog.hide()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    shortToast("Successfully left the mess")
                    // Refresh user data and return to main screen
                    viewModel.syncCurrentMessUser()
                    finish()
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to leave mess"
                    shortToast(errorMessage)
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("MessInfoActivity", "Error leaving mess", e)
                shortToast("Error leaving mess: ${e.message}")
            }
        }
    }

    private fun closeMess() {
        lifecycleScope.launch {
            try {
                loadingDialog.show()
                val response = viewModel.closeMess()
                loadingDialog.hide()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    shortToast("Mess closed successfully")
                    // Refresh user data and return to main screen
                    viewModel.syncCurrentMessUser()
                    finish()
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to close mess"
                    shortToast(errorMessage)
                }
            } catch (e: Exception) {
                loadingDialog.hide()
                Log.e("MessInfoActivity", "Error closing mess", e)
                shortToast("Error closing mess: ${e.message}")
            }
        }
    }

    private fun openAvailableMesses() {
        val intent = Intent(this, AvailableMessesActivity::class.java)
        startActivity(intent)
    }

    private fun openMyJoinRequests() {
        val intent = Intent(this, MyJoinRequestsActivity::class.java)
        startActivity(intent)
    }

    private fun openIncomingJoinRequests() {
        val intent = Intent(this, IncomingJoinRequestsActivity::class.java)
        startActivity(intent)
    }
}