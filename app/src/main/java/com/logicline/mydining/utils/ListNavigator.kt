package com.logicline.mydining.utils

import com.logicline.mydining.models.Month

class ListNavigator(private val list: List<Month>, private val monthId:Int? =null) {
    private var currentIndex = 0

    init {
        list.forEachIndexed { index, item ->
            if (item.id == monthId) {
                currentIndex = index
                return@forEachIndexed
            }
        }
    }

    fun getCurrentItem(): Month? {
        if(currentIndex>list.size || list.isEmpty()){
            return null
        }
        return list[currentIndex]
    }

    fun getNextItem(): Month? {
        val nextIndex = currentIndex - 1
        return if (nextIndex >= 0) {
            currentIndex = nextIndex
            list[nextIndex]
        } else {
            null
        }
    }

    fun getPreviousItem(): Month? {
        val prevIndex = currentIndex + 1
        return if (prevIndex < list.size) {
            currentIndex = prevIndex
            list[prevIndex]
        } else {
            null
        }
    }
}