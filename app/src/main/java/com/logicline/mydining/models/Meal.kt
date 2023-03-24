package com.logicline.mydining.models


import com.google.gson.annotations.SerializedName

data class Meal(
    @SerializedName("breakfast")
    var breakfast: String? = "",
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("dinner")
    var dinner: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("lunch")
    var lunch: String? = "",
    @SerializedName("manager_id")
    var managerId: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("user_id")
    var userId: String? = ""
)