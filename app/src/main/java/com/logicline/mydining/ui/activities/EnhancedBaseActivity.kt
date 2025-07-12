package com.logicline.mydining.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.logicline.mydining.MyApplication
import com.logicline.mydining.R
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.data.models.response.GenericRespose
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.*
import com.logicline.mydining.utils.Ext.MyExtensions.handle
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.maruf.jdialog.JDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Enhanced base activity with comprehensive data validation and user state management
 */
@AndroidEntryPoint
abstract class EnhancedBaseActivity : AppCompatActivity() {
    
    protected val userViewModel: UserViewModel by viewModels()
    protected var userData: UserData? = null
    protected open lateinit var loadingDialog: LoadingDialog

    // Override these in child activities
    protected open val requiredPermissions: List<MessPermission> = emptyList()
    protected open val requiresActiveMonth: Boolean = false
    protected open val requiresMessMembership: Boolean = true
    protected open val checkUserInitiate: Boolean = false
    protected open val validateOnCreate: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        loadingDialog = LoadingDialog(this)
        
        if (validateOnCreate) {
            setupUserDataObserver()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) {
            super.attachBaseContext(LangUtils.applyLanguage(newBase))
        } else {
            super.attachBaseContext(newBase)
        }
    }

    override fun onStart() {
        super.onStart()
        if (Constant.isManagerOrSuperUser() && checkUserInitiate) {
            checkIsMemberInitiate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupUserDataObserver() {
        lifecycleScope.launch {
            userViewModel.observedUserData.collect { state ->
                state.handle(
                    onLoading = {
                        loadingDialog.show()
                    },
                    onSuccess = { userData ->
                        loadingDialog.hide()
                        this@EnhancedBaseActivity.userData = userData
                        handleUserDataReceived(userData)
                    },
                    onError = { error ->
                        loadingDialog.hide()
                        Log.e(this@EnhancedBaseActivity::class.simpleName, "User data error: $error")
                        handleUserDataError(error.toString())
                    }
                )
            }
        }
    }

    private fun handleUserDataReceived(userData: UserData?) {
        // Validate app initialization
        val initResult = DataValidator.validateAppInitialization(userData)
        
        when (initResult) {
            AppInitializationResult.PROCEED_TO_LOGIN -> {
                redirectToLogin()
                return
            }
            AppInitializationResult.PROCEED_TO_MESS_SETUP -> {
                if (requiresMessMembership) {
                    redirectToMessSetup()
                    return
                }
            }
            AppInitializationResult.PROCEED_TO_MAIN -> {
                // Continue with activity initialization
            }
        }

        // Validate permissions if required
        if (requiredPermissions.isNotEmpty()) {
            val permissionResult = DataValidator.validateMessUserPermissions(
                userData?.messUser,
                requiredPermissions
            )
            
            if (permissionResult != PermissionValidationResult.VALID) {
                handleInsufficientPermissions(permissionResult)
                return
            }
        }

        // If all validations pass, proceed with activity-specific initialization
        onUserDataValidated(userData)
    }

    private fun handleUserDataError(error: String) {
        shortToast("Error loading user data: $error")
        onUserDataError(error)
    }

    private fun handleInsufficientPermissions(result: PermissionValidationResult) {
        val message = when (result) {
            PermissionValidationResult.NO_MESS_USER -> "You are not a member of any mess"
            PermissionValidationResult.USER_LEFT_MESS -> "You have left the mess"
            PermissionValidationResult.NO_ROLE -> "No role assigned"
            PermissionValidationResult.NO_PERMISSIONS -> "No permissions assigned"
            PermissionValidationResult.INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            else -> "Permission validation failed"
        }
        
        shortToast(message)
        onInsufficientPermissions(result)
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun redirectToMessSetup() {
        val intent = Intent(this, MessInfoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun checkIsMemberInitiate() {
        Coroutines.main {
            val res = (application as MyApplication)
                .myApi
                .isUserInitiate()

            if (res.isSuccessful && res.body() != null) {
                if (!res.body()!!.error) {
                    // User already initiated for this month
                    onUserInitiationChecked(true)
                } else {
                    // User not initiated for this month
                    showInitiationDialog(res.body()?.msg)
                }
            } else {
                onUserInitiationCheckFailed("Failed to check user initiation status")
            }
        }
    }

    private fun showInitiationDialog(message: String?) {
        JDialog.make(this)
            .setCancelable(true)
            .setBodyText(message)
            .setIconType(JDialog.IconType.WARNING)
            .setShowNegativeButton(true)
            .setShowPositiveButton(true)
            .setPositiveButtonText(getString(R.string.start_new_month))
            .setNegativeButtonText(getString(R.string.cancel))
            .setOnGenericDialogListener(object : JDialog.OnGenericDialogListener {
                override fun onPositiveButtonClick(dialog: JDialog?) {
                    startCurrentMonth(dialog)
                }

                override fun onNegativeButtonClick(dialog: JDialog?) {
                    dialog?.hideDialog()
                    onUserInitiationDeclined()
                }

                override fun onToast(message: String?) {}
            }).build().showDialog()
    }

    private fun startCurrentMonth(dialog: JDialog?) {
        val progressDialog = LoadingDialog(this)
        progressDialog.show()
        
        (application as MyApplication)
            .myApi
            .initiateAllUser(Constant.getCurrentYear(), Constant.getCurrentMonthNumber())
            .enqueue(object : Callback<GenericRespose> {
                override fun onResponse(
                    call: Call<GenericRespose>,
                    response: Response<GenericRespose>
                ) {
                    progressDialog.hide()
                    if (response.isSuccessful && response.body() != null) {
                        if (!response.body()!!.error) {
                            dialog?.hideDialog()
                            onMonthInitiated()
                        } else {
                            shortToast(response.body()!!.msg)
                            onMonthInitiationFailed(response.body()!!.msg.toString())
                        }
                    } else {
                        shortToast("Failed to initiate month")
                        onMonthInitiationFailed("Server error")
                    }
                }

                override fun onFailure(call: Call<GenericRespose>, t: Throwable) {
                    progressDialog.hide()
                    shortToast("Network error: ${t.message}")
                    onMonthInitiationFailed(t.message ?: "Network error")
                }
            })
    }

    // Methods to override in child activities
    
    /**
     * Called when user data is validated and activity can proceed with initialization
     */
    protected abstract fun onUserDataValidated(userData: UserData?)

    /**
     * Called when there's an error loading user data
     */
    protected open fun onUserDataError(error: String) {
        // Default implementation - child activities can override
    }

    /**
     * Called when user doesn't have sufficient permissions
     */
    protected open fun onInsufficientPermissions(result: PermissionValidationResult) {
        // Default implementation - child activities can override
        finish() // Close activity by default
    }

    /**
     * Called when user initiation check is completed
     */
    protected open fun onUserInitiationChecked(isInitiated: Boolean) {
        // Default implementation - child activities can override
    }

    /**
     * Called when user initiation check fails
     */
    protected open fun onUserInitiationCheckFailed(error: String) {
        // Default implementation - child activities can override
    }

    /**
     * Called when user declines to initiate month
     */
    protected open fun onUserInitiationDeclined() {
        // Default implementation - child activities can override
        finish()
    }

    /**
     * Called when month initiation is successful
     */
    protected open fun onMonthInitiated() {
        // Default implementation - child activities can override
        finish() // Restart activity to refresh data
    }

    /**
     * Called when month initiation fails
     */
    protected open fun onMonthInitiationFailed(error: String) {
        // Default implementation - child activities can override
    }

    // Utility methods

    /**
     * Utility method to check if user has specific permission
     */
    protected fun hasPermission(permission: MessPermission): Boolean {
        return userData?.messUser?.let { messUser ->
            DataValidator.validateMessUserPermissions(messUser, listOf(permission)) == PermissionValidationResult.VALID
        } ?: false
    }

    /**
     * Utility method to get current user's mess user object safely
     */
    protected fun getCurrentMessUser() = userData?.messUser

    /**
     * Utility method to get current user object safely
     */
    protected fun getCurrentUser() = userData?.user

    /**
     * Utility method to get current mess object safely
     */
    protected fun getCurrentMess() = userData?.messUser?.mess

    /**
     * Utility method to refresh user data
     */
    protected fun refreshUserData() {
        userViewModel.syncCurrentMessUser()
    }

    /**
     * Manual validation trigger for activities that don't validate on create
     */
    protected fun startValidation() {
        setupUserDataObserver()
    }
}
