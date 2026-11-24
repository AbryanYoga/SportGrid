package com.abryan.sportgrid.data.remote.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ApiResponse<T>(
    val get: String? = null,
    val parameters: Map<String, String>? = null,
    val errors: JsonElement? = null,
    val results: Int? = null,
    val response: List<T> = emptyList()
) {
    /**
     * Checks if response contains API quota or authentication errors.
     */
    val hasErrors: Boolean
        get() = errors != null && errors.toString() != "[]" && errors.toString() != "{}"
}
