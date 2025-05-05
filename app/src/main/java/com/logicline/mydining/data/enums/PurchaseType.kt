package com.logicline.mydining.data.enums

enum class PurchaseType(val value: String) {
    PURCHASE("purchase"),
    OTHER_PURCHASE("other-cost");

    companion object {
        fun fromValue(value: String): PurchaseType? {
            return entries.find { it.value == value }
        }
    }
}
