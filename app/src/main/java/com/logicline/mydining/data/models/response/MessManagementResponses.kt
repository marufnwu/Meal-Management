package com.logicline.mydining.data.models.response

import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.models.MessUser
import com.logicline.mydining.data.models.User
import com.logicline.mydining.utils.CarbonDate

data class MessInfoResponse(
    val mess: MessDetail,
    val user_role: MessUserRole,
    val permissions: List<String>,
    val is_admin: Boolean,
    val joined_at: String,
    val status: String
)

data class MessDetail(
    val id: Int,
    val name: String,
    val status: String,
    val ad_free: Boolean,
    val all_user_add_meal: Boolean,
    val fund_add_enabled: Boolean,
    val created_at: String,
    val updated_at: String,
    val is_accepting_members: Boolean,
    val active_month: Month? = null
)

data class MessUserRole(
    val id: Int,
    val role: String,
    val is_admin: Boolean,
    val permissions: List<String>
)


data class AvailableMess(
    val mess: Mess,
    val member_count: Int,
    val is_accepting_members: Boolean,
    val join_request_exists: Boolean
)

data class JoinRequestResponse(
    val join_request: JoinRequest
)

data class JoinRequest(
    val id: Int,
    val mess_id: Int,
    val mess_name: String,
    val user_id: Int,
    val status: String,
    val message: String?,
    val requested_at: String
)


data class UserJoinRequest(
    val id: Int,
    val user_name: String,
    val user_id: Int,
    val old_mess_user_id: Int?,
    val new_mess_user_id: Int?,
    val request_date: CarbonDate,
    val accept_date: CarbonDate?,
    val old_mess_id: Int?,
    val new_mess_id: Int,
    val accept_by: Int?,
    val status: Int,
    val created_at: CarbonDate,
    val updated_at: CarbonDate,
    val model_name: String,
    val new_mess: Mess,
    val old_mess: Mess?,
    val old_mess_user: MessUser?,
    val new_mess_user: MessUser?,
    val accepted_by: User?,
    val user: User?
)



data class IncomingJoinRequestsResponse(
    val join_requests: List<IncomingJoinRequest>
)

data class IncomingJoinRequest(
    val id: Int,
    val old_user_id: Int,
    val new_mess_id: Int,
    val status: String,
    val rejection_reason: String?,
    val created_at: String,
    val updated_at: String,
    val user: JoinRequestUser
)

data class JoinRequestUser(
    val id: Int,
    val name: String,
    val email: String,
    val avatar: String?
)

data class AcceptJoinRequestResponse(
    val id: Int,
    val status: String,
    val mess_user: MessUserDetail
)

data class MessUserDetail(
    val id: Int,
    val user_id: Int,
    val mess_id: Int,
    val mess_role_id: Int,
    val joined_at: String,
    val left_at: String?,
    val status: String
)

data class RejectJoinRequestResponse(
    val id: Int,
    val status: String,
    val rejection_reason: String?
)
