package com.logicline.mydining.data.repository

import com.logicline.mydining.network.MyApi

class MonthRepository(val myApi: MyApi) {
    suspend fun getMonths() = myApi.getMonths()
}