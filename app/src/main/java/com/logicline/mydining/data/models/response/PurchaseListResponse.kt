package com.logicline.mydining.data.models.response


import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.models.Purchase

data class PurchaseListResponse(
    @SerializedName("total_price")
    var totalPurchase: Float = 0f,
    @SerializedName("purchases")
    var purchases: List<Purchase> = listOf()
)