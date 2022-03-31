package com.maruf.messmanagement.models.response


import com.google.gson.annotations.SerializedName

data class DepositsResponse(
    @SerializedName("deposits")
    var deposits: List<Deposit> = listOf(),
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String = "",
    @SerializedName("totalDeposit")
    var totalDeposit: Int = 0
)