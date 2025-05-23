package com.logicline.mydining.data.models

import com.google.gson.annotations.SerializedName

data class UserDetailsSummary(
    @SerializedName("month") val month: Month,
    @SerializedName("mess_user") val messUser: MessUser,

)

