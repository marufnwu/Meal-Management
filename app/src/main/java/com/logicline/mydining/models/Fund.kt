package com.logicline.mydining.models

data class Fund(
    val amount: Int,
    val comment: String,
    var date: String,
    val id: Int,
    val manager_id: Int,
    val mess_id: Int
)