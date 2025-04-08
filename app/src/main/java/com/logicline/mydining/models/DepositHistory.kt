package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

class DepositHistory (
    @SerializedName("deposits")
    var deposits: List<Deposit> = listOf(),
    @SerializedName("total_amount")
    var totalAmount: Float = 0f,
)