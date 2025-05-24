package com.logicline.mydining.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PurchaseRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("date") var date: String? = null,
    @SerializedName("mess_user_id") var messUserId: Int,
    @SerializedName("mess_id") var messId: Int,
    @SerializedName("type") var type: String? = null,
    @SerializedName("price") var price: Double, // Changed to Double for decimal support
    @SerializedName("product") var product: String? = null,
    @SerializedName("product_json") var productJson: List<ProductItem>? = null,
    @SerializedName("deposit_request") var depositRequest: Boolean,
    @SerializedName("status") var status: Int,
    @SerializedName("purchase_type") var purchaseType: String, // Changed to String
    @SerializedName("comment") var comment: String? = null,
    @SerializedName("month_id") var monthId: Int,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("name") var name: String? = null // Kept this although not in example response
) : Parcelable {

    // Nested class for product_json items
    data class ProductItem(
        @SerializedName("name") var name: String? = null,
        @SerializedName("quantity") var quantity: Int = 0,
        @SerializedName("unit_price") var unitPrice: Double = 0.0
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readDouble()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeInt(quantity)
            parcel.writeDouble(unitPrice)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<ProductItem> {
            override fun createFromParcel(parcel: Parcel): ProductItem = ProductItem(parcel)
            override fun newArray(size: Int): Array<ProductItem?> = arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.createTypedArrayList(ProductItem.CREATOR),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(date)
        parcel.writeInt(messUserId)
        parcel.writeInt(messId)
        parcel.writeString(type)
        parcel.writeDouble(price)
        parcel.writeString(product)
        parcel.writeTypedList(productJson)
        parcel.writeByte(if (depositRequest) 1 else 0)
        parcel.writeInt(status)
        parcel.writeString(purchaseType)
        parcel.writeString(comment)
        parcel.writeInt(monthId)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
        parcel.writeString(name)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PurchaseRequest> {
        override fun createFromParcel(parcel: Parcel): PurchaseRequest = PurchaseRequest(parcel)
        override fun newArray(size: Int): Array<PurchaseRequest?> = arrayOfNulls(size)
    }
}