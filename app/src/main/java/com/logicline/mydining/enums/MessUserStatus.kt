package com.logicline.mydining.enums

enum class MessUserStatus (val value: String) {
    ACTIVE("active"),
    INACTIVE("inactive");

    companion object {
        fun fromValue(value: String): MessUserStatus {
            return entries.firstOrNull { it.value == value } ?: INACTIVE
        }
    }
}