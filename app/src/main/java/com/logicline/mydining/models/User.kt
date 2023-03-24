package com.logicline.mydining.models



import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

 object UserType{
    const val NORMAL_USER : Int = 1
    const val MANAGER : Int = 2
    const val SUPER_USER : Int = 3
}

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

    @SerializedName("mess_id")
    var messId: String? = ""

    @SerializedName("mess_name")
    var messName: String? = ""

    @SerializedName("user_name")
    var userName: String? = ""

    @SerializedName("city")
    var city: String? = ""
    @SerializedName("email")
    var email: String? = ""
    @SerializedName("gender")
    var gender: String? = ""

    @SerializedName("country")
    var country: String? = ""

    @SerializedName("ad_free")
    var adFree: Int = 1

    @SerializedName("all_user_add_meal")
    var allUserAddMeal: Int = 0

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
        messId = parcel.readString()
        messName = parcel.readString()
        userName = parcel.readString()
        city = parcel.readString()
        email = parcel.readString()
        gender = parcel.readString()
        country = parcel.readString()
        adFree = parcel.readInt()
        allUserAddMeal = parcel.readInt()
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
        parcel.writeString(messId)
        parcel.writeString(messName)
        parcel.writeString(userName)
        parcel.writeString(city)
        parcel.writeString(email)
        parcel.writeString(gender)
        parcel.writeString(country)
        parcel.writeInt(adFree)
        parcel.writeInt(allUserAddMeal)
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