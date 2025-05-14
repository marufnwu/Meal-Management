package com.logicline.mydining.data

sealed class DataState<T>(
    val data: T? = null,
    val message: String? = null,
    val throwable: Throwable? = null
) {
    class Idle<T> : DataState<T>()
    class Loading<T> : DataState<T>()
    class Success<T>(data: T, message: String? = null) : DataState<T>(data, message)
    class Error<T>(data: T? = null, message: String? = null) : DataState<T>(data, message)
    class Exception<T>(
        exception: Throwable? = null,
        message: String? = null,
        data: T? = null
    ) : DataState<T>(data, message, exception)
}
