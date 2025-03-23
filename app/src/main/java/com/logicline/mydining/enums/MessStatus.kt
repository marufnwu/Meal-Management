package com.logicline.mydining.enums

enum class MessStatus(val value : String) {
    ACTIVE("active"),
    DEACTIVATED("deactivated"),
    DELETED ("deleted");

    companion object {
        fun fromValue(value: String): MessStatus? {
            return MessStatus.values().find { it.value == value }
        }
    }
}