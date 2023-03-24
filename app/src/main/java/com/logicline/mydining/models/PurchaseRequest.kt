package com.logicline.mydining.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PurchaseRequest (
    @SerializedName("id"              ) var id             : Int,
    @SerializedName("date"            ) var date           : String?,
    @SerializedName("user_id"         ) var userId         : Int,
    @SerializedName("mess_id"         ) var messId         : Int,
    @SerializedName("type"            ) var type           : String?,
    @SerializedName("price"           ) var price          : Int,
    @SerializedName("product"         ) var product        : String? = null,
    @SerializedName("product_json"    ) var productJson    : String? = null,
    @SerializedName("deposit_request" ) var depositRequest : Int,
    @SerializedName("status"          ) var status         : Int,
    @SerializedName("purchase_type"   ) var purchase_type         : Int,
    @SerializedName("comment"         ) var comment        : String? = null,
    @SerializedName("name"            ) var name        : String? = null
):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(date)
        parcel.writeInt(userId)
        parcel.writeInt(messId)
        parcel.writeString(type)
        parcel.writeInt(price)
        parcel.writeString(product)
        parcel.writeString(productJson)
        parcel.writeInt(depositRequest)
        parcel.writeInt(status)
        parcel.writeInt(purchase_type)
        parcel.writeString(comment)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PurchaseRequest> {
        override fun createFromParcel(parcel: Parcel): PurchaseRequest {
            return PurchaseRequest(parcel)
        }

        override fun newArray(size: Int): Array<PurchaseRequest?> {
            return arrayOfNulls(size)
        }
    }
}