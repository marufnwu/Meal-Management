package com.maruf.messmanagement.models


import com.google.gson.annotations.SerializedName

data class Purchase(
    @SerializedName("date")
    var date: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("manager_id")
    var managerId: String = "",
    @SerializedName("price")
    var price: String = "",
    @SerializedName("product")
    var product: String = "",
    @SerializedName("user")
    var user: String = "",
    @SerializedName("user_id")
    var userId: String = ""
)