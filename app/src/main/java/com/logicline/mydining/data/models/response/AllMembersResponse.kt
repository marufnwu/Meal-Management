package com.logicline.mydining.data.models.response

import com.logicline.mydining.data.models.MessUser

data class AllMembersResponse(
    val status : String,
    val users : List<MessUser>
)