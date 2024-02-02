package com.logicline.mydining.models

data class Month (
    val id : Int,
    val name:String,
    val open_at : String,
    val close_at : String,
    val status : Int,
    val user_id : Int
)