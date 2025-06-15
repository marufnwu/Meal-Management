package com.logicline.mydining.data.enums

enum class PurchaseType(val value: String) {
    MEAL("meal"),
    OTHER("other");

    companion object {
        fun fromValue(value: String): PurchaseType? {
            return entries.find { it.value == value }
        }
    }
}
