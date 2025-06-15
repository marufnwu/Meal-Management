package com.logicline.mydining.data.repository

import com.logicline.mydining.data.requests.MonthCreateRequest
import com.logicline.mydining.network.MyApi

class MonthRepository(val myApi: MyApi) {
    suspend fun getMonths() = myApi.getMonths()
    suspend fun createMonth(
        name: String?,
        type: String,
        month: Int?,
        year: Int?,
        startAt: String?,
        forceCloseOther: Boolean
    ) = myApi.createMonth(
        MonthCreateRequest(
            name = name,
            type = type,
            month = month,
            year = year,
            start_at = startAt,
            force_close_other = forceCloseOther
        )
    )
}