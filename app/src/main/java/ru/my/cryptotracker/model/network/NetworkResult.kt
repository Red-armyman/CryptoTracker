package ru.my.cryptotracker.model.network

sealed interface NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>
    data class Error(val message: String) : NetworkResult<Nothing>
    object Loading : NetworkResult<Nothing>
}