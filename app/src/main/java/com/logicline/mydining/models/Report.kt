package com.logicline.mydining.models

data class Report(
    val creation_date: String,
    val id: Int,
    val mess_id: Int,
    val month: Int,
    val name: String,
    val pdf: String,
    val type: String,
    val user_id: Int,
    val year: Int
)