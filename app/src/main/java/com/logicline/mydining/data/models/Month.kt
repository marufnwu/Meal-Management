package com.logicline.mydining.data.models

import com.google.gson.annotations.SerializedName


data class Month(
    @SerializedName("id")
    var id: Int,
    @SerializedName("mess_id")
    var messId: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("start_at")
    var startAt: String,
    @SerializedName("end_at")
    var endAt: String,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("updated_at")
    var updatedAt: String,
    @SerializedName("is_active")
    var isActive: Boolean
)