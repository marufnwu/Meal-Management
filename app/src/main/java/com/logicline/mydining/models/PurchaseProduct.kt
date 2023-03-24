package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

data class PurchaseProduct(
    @SerializedName("name") var name : String? = null,
    @SerializedName("price") var price : Float? = null,

)
