package com.logicline.mydining.ui.adapters

import com.logicline.mydining.data.enums.MessUserStatus
import com.logicline.mydining.data.models.MessUser

/**
 * Helper object to determine user spinner item states based on business logic
 */
object UserSpinnerHelper {
    
    /**
     * Determines if a MessUser should be enabled in the spinner
     * You can customize this logic based on your requirements
     */
    fun isUserEnabled(messUser: MessUser?): Boolean {
        return when {
            messUser == null -> false
            messUser.user == null -> false
            
            // Example conditions - customize these based on your needs:
            
            // 1. Disable users with specific status
            messUser.status == MessUserStatus.INACTIVE.value -> false
            
            // 2. Disable users whose names contain certain keywords
            messUser.user.name?.contains("disabled", ignoreCase = true) == true -> false
            messUser.user.name?.contains("suspended", ignoreCase = true) == true -> false

            // 3. Disable users who left the mess
            messUser.isUserLeftMess -> false
            
            // 3. Disable users based on some other property
            // messUser.user.email?.isEmpty() == true -> false
            
            // 4. Enable all other users
            else -> true
        }
    }
    
    /**
     * Get display text for a MessUser
     */
    fun getDisplayText(messUser: MessUser?): String {
        return when {
            messUser?.user?.name?.isNotEmpty() == true -> messUser.user.name
            messUser?.user?.email?.isNotEmpty() == true -> messUser.user.email
            else -> "Unknown User"
        }
    }
    
    /**
     * Create user spinner items from a list of MessUsers
     */
    fun createUserSpinnerItems(userList: List<MessUser>, headerText: String = "Select User"): List<UserSpinnerItem> {
        val spinnerItems = mutableListOf<UserSpinnerItem>()
        
        // Add header
        spinnerItems.add(UserSpinnerItem(null, headerText, true, true))
        
        // Add users
        userList.forEach { messUser ->
            val displayText = getDisplayText(messUser)
            val isEnabled = isUserEnabled(messUser)
            spinnerItems.add(UserSpinnerItem(messUser, displayText, isEnabled, false))
        }
        
        return spinnerItems
    }
}