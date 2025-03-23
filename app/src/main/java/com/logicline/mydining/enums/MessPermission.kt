package com.logicline.mydining.enums

import com.logicline.mydining.enums.MessPermission.Companion.hasAnyPermission
import com.logicline.mydining.models.MessUser

enum class MessPermission(val value: String) {
    // Existing permissions
    USER_MANAGEMENT("user_management"),
    MEAL_MANAGEMENT("meal_management"),
    PURCHASE_MANAGEMENT("purchase_management"),
    DEPOSIT_MANAGEMENT("deposit-management"),
    REPORT_MANAGEMENT("report-management"),
    NOTICE_MANAGEMENT("notice-management"),
    PERMISSION_MANAGEMENT("notice-management"),

    // New permissions
    USER_ADD("user_add"),
    USER_REMOVE("user_remove"),
    USER_EDIT("user_edit"),
    MEAL_ADD("meal_add"),
    MEAL_EDIT("meal_edit"),
    MEAL_DELETE("meal_delete"),
    PURCHASE_ADD("purchase_add"),
    PURCHASE_EDIT("purchase_edit"),
    PURCHASE_DELETE("purchase_delete"),
    DEPOSIT_ADD("deposit_add"),
    DEPOSIT_REMOVE("deposit_remove"),
    DEPOSIT_DELETE("deposit_delete"),
    GENERATE_REPORT("generate_report"),
    SEND_NOTIFICATION("send_notification"),
    NOTICE_ADD("notice_add");



    companion object {
        fun fromValue(value: String): MessPermission? {
            return values().find { it.value == value }
        }

        fun MessUser?.hasAllPermission(vararg messPermissions: MessPermission): Boolean {
            if(this == null){
                return false;
            }

            if(this.role == null){
                return false;
            }

            if(this.role.isAdmin){
                return true;
            }

            val permissionValues = this.role.permissions?.map { it.permission } ?: return false
            return messPermissions.all { permission ->
                permission.value in permissionValues
            }
        }

        fun MessUser?.hasAnyPermission(vararg messPermissions: MessPermission): Boolean {
            if(this == null){
                return false;
            }
            if(this.role == null){
                return false;
            }

            if(this.role.isAdmin){
                return true;
            }

            return this.role.permissions?.any { p ->
                p.permission in messPermissions.map { it.value }
            } ?: false
        }

    }
}

