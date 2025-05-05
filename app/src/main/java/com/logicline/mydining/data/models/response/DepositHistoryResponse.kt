package com.logicline.mydining.data.models.response

import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.DepositHistory

class DepositHistoryResponse(
    @SerializedName("total")
    var total: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("history")
    var history: MutableList<DepositHistory> = mutableListOf(),
)