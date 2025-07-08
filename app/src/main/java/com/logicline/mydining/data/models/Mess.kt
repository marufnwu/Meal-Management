package com.logicline.mydining.data.models

import android.R
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.logicline.mydining.data.local.entities.MessEntity
import com.logicline.mydining.utils.CarbonDate

data class Mess(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("status") val status: String,
    @SerializedName("ad_free") val adFree: Boolean,
    @SerializedName("all_user_add_meal") val allUserAddMeal: Boolean,
    @SerializedName("fund_add_enabled") val fundAddEnabled: Boolean,
    @SerializedName("created_at") val createdAt: CarbonDate?,
    @SerializedName("updated_at") val updatedAt: CarbonDate?,
    @SerializedName("is_accepting_members") val isAcceptingMembers: Boolean = true
) : Parcelable {
    
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()?.let { CarbonDate(it) },
        parcel.readString()?.let { CarbonDate(it) },
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(status)
        parcel.writeByte(if (adFree) 1 else 0)
        parcel.writeByte(if (allUserAddMeal) 1 else 0)
        parcel.writeByte(if (fundAddEnabled) 1 else 0)
        parcel.writeString(createdAt?.raw)
        parcel.writeString(updatedAt?.raw)
        parcel.writeByte(if (isAcceptingMembers) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mess> {
        override fun createFromParcel(parcel: Parcel): Mess {
            return Mess(parcel)
        }

        override fun newArray(size: Int): Array<Mess?> {
            return arrayOfNulls(size)
        }
    }
}


fun Mess.toEntity(): MessEntity {
    return MessEntity(
        id = id,
        name = name,
        status = status,
        adFree = adFree,
        allUserAddMeal = allUserAddMeal,
        fundAddEnabled = fundAddEnabled,
        createdAt = createdAt?.raw,
        updatedAt = updatedAt?.raw
    )
}