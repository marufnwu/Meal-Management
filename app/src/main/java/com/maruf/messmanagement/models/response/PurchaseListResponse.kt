package com.maruf.messmanagement.models.response


import com.google.gson.annotations.SerializedName
import com.maruf.messmanagement.models.Purchase

data class PurchaseListResponse(
    @SerializedName("error")
    var error: Boolean = false,
    @SerializedName("msg")
    var msg: String = "",
    @SerializedName("totalPurchase")
    var totalPurchase: Int = 0,
    @SerializedName("purchases")
    var purchases: List<Purchase> = listOf()
)