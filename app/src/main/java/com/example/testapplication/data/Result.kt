package com.example.testapplication.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }
}

data class UiState<T>(
    val dataList: List<T>? = null,
    val dataSingle: T? = null,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
) {
    val status: Status
        get() = if (isLoading) {
            Status.LOADING
        } else if (isError) {
            Status.ERROR
        } else {
            Status.DATA
        }
}


enum class Status {
    LOADING, ERROR, DATA
}