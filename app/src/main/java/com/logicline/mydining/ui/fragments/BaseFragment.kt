package com.logicline.mydining.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.models.UserData
import com.logicline.mydining.ui.activities.LoginActivity
import com.logicline.mydining.ui.activities.MessInfoActivity
import com.logicline.mydining.ui.viewmodels.UserViewModel
import com.logicline.mydining.utils.AppInitializationResult
import com.logicline.mydining.utils.DataValidator
import com.logicline.mydining.utils.Ext.MyExtensions.handle
import com.logicline.mydining.utils.Ext.MyExtensions.shortToast
import com.logicline.mydining.utils.LoadingDialog
import com.logicline.mydining.utils.PermissionValidationResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Base fragment that provides common data validation and user state management
 */
@AndroidEntryPoint
abstract class BaseFragment : Fragment() {

    protected val userViewModel: UserViewModel by viewModels()
    protected var userData: UserData? = null
    protected lateinit var loadingDialog: LoadingDialog

    // Override these in child fragments
    protected open val requiredPermissions: List<MessPermission> = emptyList()
    protected open val requiresActiveMonth: Boolean = false
    protected open val requiresMessMembership: Boolean = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadingDialog = LoadingDialog(requireActivity())
        setupUserDataObserver()
    }

    private fun setupUserDataObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.observedUserData.collect { state ->
                state.handle(
                    onLoading = {
                        loadingDialog.show()
                    },
                    onSuccess = { userData ->
                        loadingDialog.hide()
                        this@BaseFragment.userData = userData
                        handleUserDataReceived(userData)
                    },
                    onError = { error ->
                        loadingDialog.hide()
                        Log.e(this@BaseFragment::class.simpleName, "User data error: $error")
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
                // Continue with fragment initialization
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

        // If all validations pass, proceed with fragment-specific initialization
        onUserDataValidated(userData)
    }

    private fun handleUserDataError(error: String) {
        requireContext().shortToast("Error loading user data: $error")
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

        requireContext().shortToast(message)
        onInsufficientPermissions(result)
    }

    private fun redirectToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun redirectToMessSetup() {
        val intent = Intent(requireContext(), MessInfoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    // Methods to override in child fragments
    
    /**
     * Called when user data is validated and fragment can proceed with initialization
     */
    protected abstract fun onUserDataValidated(userData: UserData?)

    /**
     * Called when there's an error loading user data
     */
    protected open fun onUserDataError(error: String) {
        // Default implementation - child fragments can override
    }

    /**
     * Called when user doesn't have sufficient permissions
     */
    protected open fun onInsufficientPermissions(result: PermissionValidationResult) {
        // Default implementation - child fragments can override
    }

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
}
