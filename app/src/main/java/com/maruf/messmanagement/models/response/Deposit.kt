package com.maruf.messmanagement.models.response


import com.google.gson.annotations.SerializedName

data class Deposit(
    @SerializedName("amount")
    var amount: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("user_id")
    var userId: String = ""
)