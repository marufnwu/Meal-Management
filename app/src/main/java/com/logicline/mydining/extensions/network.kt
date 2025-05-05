package com.logicline.mydining.extensions

import com.logicline.mydining.data.DataState
import com.logicline.mydining.data.models.response.ServerResponse
import retrofit2.Response

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ServerResponse<T>>): DataState<T?> {
    return try {
        val response = apiCall()

        val body = response.body()

        if (response.isSuccessful && body != null) {
            if (!body.error) {
                DataState.Success(
                    data = body.data,
                    message = body.msg
                )
            } else {

                val combinedMessage = buildString {
                    append(body.msg)
                    if (!body.errors.isNullOrEmpty()) {
                        append("\n")
                        append(body.errors!!.joinToString("\n"))
                    }
                }

                DataState.Error(
                    data = body.data,
                    message = combinedMessage

                )
            }
        } else {
            DataState.Error(
                message = response.message()
            )
        }
    } catch (e: Exception) {
        DataState.Error(
            message = e.localizedMessage ?: "Unknown error occurred"
        )
    }
}

fun <T, R> DataState<T>.map(transform: (T) -> R): DataState<R> {
    return when (this) {
        is DataState.Success -> DataState.Success(transform(this.data!!), this.message)
        is DataState.Error -> DataState.Error(data = null, message = this.message)
        is DataState.Loading -> DataState.Loading()
        is DataState.Idle -> DataState.Idle()
    }
}
