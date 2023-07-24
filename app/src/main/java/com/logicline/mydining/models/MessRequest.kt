package com.logicline.mydining.models

data class MessRequest(
    val accept_by: Int,
    val accept_date: String,
    val id: Int,
    val new_mess_id: Int,
    val new_user_id: Any,
    val old_mess_id: Int,
    val old_user_id: Int,
    val request_date: String,
    val status: Int,
    val user_name: String,
    val mess_name: String,
    val name: String,
)