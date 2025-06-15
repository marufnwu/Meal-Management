package com.logicline.mydining.data.requests

data class MonthCreateRequest(
    val name: String?,
    val type: String,
    val month: Int?,
    val year: Int?,
    val start_at: String?,
    val force_close_other: Boolean
)