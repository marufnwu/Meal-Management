package com.logicline.mydining.data.enums

enum class MessStatus(val value : String) {
    ACTIVE("active"),
    DEACTIVATED("deactivated"),
    DELETED ("deleted");

    companion object {
        fun fromValue(value: String): MessStatus? {
            return values().find { it.value == value }
        }
    }
}