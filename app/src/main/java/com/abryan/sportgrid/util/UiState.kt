package com.abryan.sportgrid.util

import androidx.annotation.StringRes

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    object Empty : UiState<Nothing>
    data class Error(
        val message: String? = null,
        @StringRes val messageRes: Int? = null
    ) : UiState<Nothing>
}
