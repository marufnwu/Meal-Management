package com.logicline.mydining.models

data class MessUser(
    val active: Boolean,
    val created_at: String,
    val id: Int,
    val mess_id: Int,
    val mess_user_id: Int,
    val model_name: String,
    val month_id: Int,
    val updated_at: String,
    val user : User?
)