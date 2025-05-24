package com.logicline.mydining.data.enums

enum class PurchaseRequestStatus(val value: Int) {
    PENDING(0),
    APPROVED(1),
    REJECTED(2);

    companion object {
        fun fromValue(value: Int): PurchaseRequestStatus? {
            return entries.find { it.value == value }
        }
    }
}
