package com.maruf.messmanagement.models



import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class User() : Parcelable{
    @SerializedName("acc_type")
    var accType: String? = ""
    @SerializedName("active")
    var active: String? = ""
    @SerializedName("fcm_token")
    var fcmToken: String? = ""
    @SerializedName("id")
    var id: String? = ""
    @SerializedName("join_date")
    var joinDate: String? = ""
    @SerializedName("leave_date")
    var leaveDate: String? = ""
    @SerializedName("name")
    var name: String? = ""
    @SerializedName("password")
    var password: String? = ""
    @SerializedName("phone")
    var phone: String? = ""
    @SerializedName("photo_url")
    var photoUrl: String? = ""

    constructor(parcel: Parcel) : this() {
        accType = parcel.readString()
        active = parcel.readString()
        fcmToken = parcel.readString()
        id = parcel.readString()
        joinDate = parcel.readString()
        leaveDate = parcel.readString()
        name = parcel.readString()
        password = parcel.readString()
        phone = parcel.readString()
        photoUrl = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accType)
        parcel.writeString(active)
        parcel.writeString(fcmToken)
        parcel.writeString(id)
        parcel.writeString(joinDate)
        parcel.writeString(leaveDate)
        parcel.writeString(name)
        parcel.writeString(password)
        parcel.writeString(phone)
        parcel.writeString(photoUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}