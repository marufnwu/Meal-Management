package com.logicline.mydining.enums

enum class PurchaseType(val type: String) {
    PURCHASE("purchase"),
    OTHER_PURCHASE("other-purchase");

    companion object {
        fun fromType(type: String): PurchaseType? {
            return values().find { it.type == type }
        }
    }
}
