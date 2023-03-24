package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

class DepositHistory (
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("user_id")
    var user_id: Int = 0,

    @SerializedName("amount")
    var amount: Float = 0f,

    @SerializedName("date")
    var date: String = "",

    @SerializedName("name")
    var name: String = "",

    @SerializedName("manager_id")
    var manager_id: Int = 0,

    @SerializedName("mess_id")
    var mess_id: Int = 0,
)