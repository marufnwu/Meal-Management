package com.logicline.mydining.models

import com.google.gson.annotations.SerializedName

data class Banner (

    @SerializedName("id"          ) var id         : Int?    = null,
    @SerializedName("name"        ) var name       : String? = null,
    @SerializedName("image_url"   ) var imageUrl   : String? = null,
    @SerializedName("action_url"  ) var actionUrl  : String? = null,
    @SerializedName("action_type" ) var actionType : Int?    = null,
    @SerializedName("visible"     ) var visible    : Int?    = null

)