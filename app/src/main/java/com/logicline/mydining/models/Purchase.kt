package com.logicline.mydining.models


import com.google.gson.annotations.SerializedName

data class Purchase(
    @SerializedName("id")
    val id: Int,

    @SerializedName("mess_user_id")
    val messUserId: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("product")
    val product: String,

    @SerializedName("month_id")
    val monthId: Int,

    @SerializedName("mess_id")
    val messId: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("mess_user")
    val messUSer: MessUser
)