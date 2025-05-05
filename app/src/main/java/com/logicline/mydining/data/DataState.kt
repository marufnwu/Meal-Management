package com.logicline.mydining.data

sealed class DataState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Idle<T> : DataState<T>()
    class Loading<T> : DataState<T>()
    class Success<T>(data: T, message: String? = null) : DataState<T>(data, message)
    class  Error<T>(data: T? = null, message: String? = null) : DataState<T>(data, message)
}
