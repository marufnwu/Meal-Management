package com.logicline.mydining.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PurchaseRequest(
    @SerializedName("id") var id: Int,
    @SerializedName("mess_user") var messUser: MessUser,
    @SerializedName("date") var date: String? = null,
    @SerializedName("mess_user_id") var messUserId: Int,
    @SerializedName("mess_id") var messId: Int,
    @SerializedName("type") var type: String? = null,
    @SerializedName("price") var price: Double,
    @SerializedName("product") var product: String? = null,
    @SerializedName("product_json") var productJson: List<ProductItem>? = null,
    @SerializedName("deposit_request") var depositRequest: Boolean,
    @SerializedName("status") var status: Int,
    @SerializedName("purchase_type") var purchaseType: String,
    @SerializedName("comment") var comment: String? = null,
    @SerializedName("month_id") var monthId: Int,
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        messUser = parcel.readParcelable(MessUser::class.java.classLoader)!!,
        date = parcel.readString(),
        messUserId = parcel.readInt(),
        messId = parcel.readInt(),
        type = parcel.readString(),
        price = parcel.readDouble(),
        product = parcel.readString(),
        productJson = parcel.createTypedArrayList(ProductItem.CREATOR),
        depositRequest = parcel.readByte() != 0.toByte(),
        status = parcel.readInt(),
        purchaseType = parcel.readString()!!,
        comment = parcel.readString(),
        monthId = parcel.readInt(),
        createdAt = parcel.readString(),
        updatedAt = parcel.readString()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeParcelable(messUser, flags)
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
    }

    companion object CREATOR : Parcelable.Creator<PurchaseRequest> {
        override fun createFromParcel(parcel: Parcel): PurchaseRequest {
            return PurchaseRequest(parcel)
        }

        override fun newArray(size: Int): Array<PurchaseRequest?> {
            return arrayOfNulls(size)
        }
    }

    // Nested class for product_json items
    data class ProductItem(
        @SerializedName("name") var name: String? = null,
        @SerializedName("quantity") var quantity: Int = 0,
        @SerializedName("unit_price") var unitPrice: Float = 0f
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            name = parcel.readString(),
            quantity = parcel.readInt(),
            unitPrice = parcel.readFloat()
        )

        override fun describeContents(): Int = 0

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeInt(quantity)
            parcel.writeFloat(unitPrice)
        }

        companion object CREATOR : Parcelable.Creator<ProductItem> {
            override fun createFromParcel(parcel: Parcel): ProductItem {
                return ProductItem(parcel)
            }

            override fun newArray(size: Int): Array<ProductItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}