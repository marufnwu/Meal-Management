package com.logicline.mydining.data.enums

enum class MessJoinRequestStatus(val value : Int) {
    PENDING(0),
    APPROVED(1),
    REJECTED (2),
    CANCELLED (3);

    companion object {
        fun fromValue(value: Int): MessJoinRequestStatus? {
            return entries.find { it.value == value }
        }
    }
}