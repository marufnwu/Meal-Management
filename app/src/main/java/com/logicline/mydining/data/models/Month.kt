package com.logicline.mydining.data.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import com.logicline.mydining.utils.CarbonDate


data class Month (
    @SerializedName("id")
    var id: Int,
    @SerializedName("mess_id")
    var messId: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("start_at")
    var startAt: CarbonDate,
    @SerializedName("end_at")
    var endAt: CarbonDate?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("is_active")
    var isActive: Boolean
) : java.io.Serializable {

    override fun toString(): String {
        return "Month(id=$id, messId=$messId, name='$name', type='$type', startAt='$startAt', endAt=$endAt, createdAt=$createdAt, updatedAt=$updatedAt, isActive=$isActive)"
    }
}