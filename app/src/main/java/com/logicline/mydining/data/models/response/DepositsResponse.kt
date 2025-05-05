package com.logicline.mydining.data.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.Deposit

data class DepositsResponse(
    @SerializedName("deposits")
    var deposits: List<Deposit> = listOf(),
    @SerializedName("total_amount")
    var totalDeposit: Int = 0
)