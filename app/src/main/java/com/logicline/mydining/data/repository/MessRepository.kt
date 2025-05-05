package com.logicline.mydining.data.repository

import com.logicline.mydining.network.MyApi

class MessRepository(val myApi: MyApi) {
    suspend fun createMess(name : String) = myApi.createMess(name)
}