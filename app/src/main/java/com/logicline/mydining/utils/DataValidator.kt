package com.logicline.mydining.utils

import com.logicline.mydining.data.enums.MessPermission
import com.logicline.mydining.data.models.*

/**
 * Utility class for validating data existence and consistency
 * Based on the backend model relationships
 */
object DataValidator {

    /**
     * Validates if user data is complete and valid for app usage
     */
    fun validateUserData(userData: UserData?): UserDataValidationResult {
        return when {
            userData == null -> UserDataValidationResult.NULL_USER_DATA
            userData.user == null -> UserDataValidationResult.NULL_USER
            userData.token.isNullOrBlank() -> UserDataValidationResult.INVALID_TOKEN
            userData.user?.emailVerifiedAt == null -> UserDataValidationResult.EMAIL_NOT_VERIFIED
            userData.messUser == null -> UserDataValidationResult.NO_MESS_ASSOCIATION
            userData?.messUser?.leftAt != null -> UserDataValidationResult.USER_LEFT_MESS
            userData?.messUser?.mess == null -> UserDataValidationResult.NULL_MESS
            userData?.messUser?.role == null -> UserDataValidationResult.NULL_ROLE
            else -> UserDataValidationResult.VALID
        }
    }

    /**
     * Validates if mess user can perform specific operations
     */
    fun validateMessUserPermissions(
        messUser: MessUser?,
        requiredPermissions: List<MessPermission>
    ): PermissionValidationResult {
        return when {
            messUser == null -> PermissionValidationResult.NO_MESS_USER
            messUser.leftAt != null -> PermissionValidationResult.USER_LEFT_MESS
            messUser.role == null -> PermissionValidationResult.NO_ROLE
            messUser.role.permissions.isNullOrEmpty() -> PermissionValidationResult.NO_PERMISSIONS
            !hasRequiredPermissions(messUser, requiredPermissions) -> PermissionValidationResult.INSUFFICIENT_PERMISSIONS
            else -> PermissionValidationResult.VALID
        }
    }

    /**
     * Validates if current month is active and user is initiated
     */
    fun validateMonthlyOperations(
        userData: UserData?,
        currentMonth: Month?
    ): MonthlyValidationResult {
        val userValidation = validateUserData(userData)
        if (userValidation != UserDataValidationResult.VALID) {
            return MonthlyValidationResult.INVALID_USER_DATA
        }

        return when {
            currentMonth == null -> MonthlyValidationResult.NO_ACTIVE_MONTH
            !currentMonth.isActive -> MonthlyValidationResult.INACTIVE_MONTH
            // Note: We need to check if user is initiated for current month
            // This requires additional API call or local data
            else -> MonthlyValidationResult.VALID
        }
    }

    /**
     * Validates mess data completeness
     */
    fun validateMessData(mess: Mess?): MessValidationResult {
        return when {
            mess == null -> MessValidationResult.NULL_MESS
            mess.name.isNullOrBlank() -> MessValidationResult.INVALID_NAME
            mess.status == "INACTIVE" -> MessValidationResult.INACTIVE_MESS
            else -> MessValidationResult.VALID
        }
    }

    /**
     * Check if user has required permissions
     */
    private fun hasRequiredPermissions(
        messUser: MessUser,
        requiredPermissions: List<MessPermission>
    ): Boolean {
        val userPermissions = messUser.role?.permissions?.map { it.permission } ?: emptyList()
        return requiredPermissions.any { required ->
            userPermissions.contains(required.name)
        }
    }

    /**
     * Comprehensive validation for app initialization
     */
    fun validateAppInitialization(userData: UserData?): AppInitializationResult {
        val userValidation = validateUserData(userData)
        
        return when (userValidation) {
            UserDataValidationResult.VALID -> {
                val messValidation = validateMessData(userData?.messUser?.mess)
                when (messValidation) {
                    MessValidationResult.VALID -> AppInitializationResult.PROCEED_TO_MAIN
                    else -> AppInitializationResult.PROCEED_TO_MESS_SETUP
                }
            }
            UserDataValidationResult.NO_MESS_ASSOCIATION -> AppInitializationResult.PROCEED_TO_MESS_SETUP
            UserDataValidationResult.USER_LEFT_MESS -> AppInitializationResult.PROCEED_TO_MESS_SETUP
            UserDataValidationResult.NULL_MESS -> AppInitializationResult.PROCEED_TO_MESS_SETUP
            else -> AppInitializationResult.PROCEED_TO_LOGIN
        }
    }
}

// Validation Result Enums
enum class UserDataValidationResult {
    VALID,
    NULL_USER_DATA,
    NULL_USER,
    INVALID_TOKEN,
    EMAIL_NOT_VERIFIED,
    NO_MESS_ASSOCIATION,
    USER_LEFT_MESS,
    NULL_MESS,
    NULL_ROLE
}

enum class PermissionValidationResult {
    VALID,
    NO_MESS_USER,
    USER_LEFT_MESS,
    NO_ROLE,
    NO_PERMISSIONS,
    INSUFFICIENT_PERMISSIONS
}

enum class MonthlyValidationResult {
    VALID,
    INVALID_USER_DATA,
    NO_ACTIVE_MONTH,
    INACTIVE_MONTH,
    USER_NOT_INITIATED
}

enum class MessValidationResult {
    VALID,
    NULL_MESS,
    INVALID_NAME,
    INACTIVE_MESS
}

enum class AppInitializationResult {
    PROCEED_TO_MAIN,
    PROCEED_TO_MESS_SETUP,
    PROCEED_TO_LOGIN
}
