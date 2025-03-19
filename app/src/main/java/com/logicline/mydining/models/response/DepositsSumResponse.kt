package com.logicline.mydining.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.models.Deposit
import com.logicline.mydining.models.DepositSum

data class DepositsSumResponse(
    @SerializedName("deposits")
    var deposits: List<DepositSum> = listOf(),
    @SerializedName("total_amount")
    var totalDeposit: Int = 0
)