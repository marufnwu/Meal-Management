package com.logicline.mydining.data.models.response

import com.logicline.mydining.data.models.Mess
import com.logicline.mydining.data.models.Month
import com.logicline.mydining.data.models.MessUser

data class MessInfoResponse(
    val mess: Mess,
    val user_role: MessUserRole,
    val member_count: Int,
    val active_month: Month?,
    val recent_activities: List<RecentActivity>?
)

data class MessUserRole(
    val id: Int,
    val role: String,
    val permissions: List<String>
)

data class RecentActivity(
    val type: String,
    val user: String,
    val date: String
)

data class AvailableMessesResponse(
    val messes: List<AvailableMess>,
    val total: Int,
    val current_user_mess_status: String
)

data class AvailableMess(
    val id: Int,
    val name: String,
    val member_count: Int,
    val created_at: String,
    val location: String?,
    val description: String?,
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

data class UserJoinRequestsResponse(
    val join_requests: List<UserJoinRequest>,
    val total: Int,
    val pending_count: Int
)

data class UserJoinRequest(
    val id: Int,
    val mess: MessInfo,
    val status: String,
    val message: String?,
    val requested_at: String,
    val updated_at: String,
    val rejection_reason: String?,
    val can_cancel: Boolean
)

data class MessInfo(
    val id: Int,
    val name: String,
    val member_count: Int
)

data class IncomingJoinRequestsResponse(
    val join_requests: List<IncomingJoinRequest>,
    val total: Int,
    val pending_count: Int
)

data class IncomingJoinRequest(
    val id: Int,
    val user: JoinRequestUser,
    val status: String,
    val message: String?,
    val requested_at: String,
    val user_background: UserBackground?
)

data class JoinRequestUser(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String?,
    val city: String?
)

data class UserBackground(
    val previous_mess_experience: Boolean?,
    val dietary_restrictions: String?
)

data class AcceptJoinRequestResponse(
    val accepted_request: AcceptedRequest,
    val new_member: NewMember,
    val welcome_message: String?
)

data class AcceptedRequest(
    val id: Int,
    val user: JoinRequestUser,
    val accepted_at: String,
    val accepted_by: String
)

data class NewMember(
    val mess_user_id: Int,
    val role: String,
    val status: String,
    val initiated_for_current_month: Boolean
)

data class RejectJoinRequestResponse(
    val rejected_request: RejectedRequest
)

data class RejectedRequest(
    val id: Int,
    val user: JoinRequestUser,
    val rejected_at: String,
    val rejected_by: String,
    val reason: String?
)
