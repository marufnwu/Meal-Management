package com.logicline.mydining.data.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.DepositSum

data class DepositsSumResponse(
    @SerializedName("deposits")
    var deposits: List<DepositSum> = listOf(),
    @SerializedName("total_amount")
    var totalDeposit: Int = 0
)