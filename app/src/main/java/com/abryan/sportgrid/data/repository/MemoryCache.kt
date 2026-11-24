package com.abryan.sportgrid.data.repository

class MemoryCache<T>(private val ttlMillis: Long) {
    private var cachedData: T? = null
    private var timestamp: Long = 0L

    val isValid: Boolean
        get() = cachedData != null && (System.currentTimeMillis() - timestamp) < ttlMillis

    fun get(): T? = if (isValid) cachedData else null

    fun getEvenIfExpired(): T? = cachedData

    fun put(data: T) {
        cachedData = data
        timestamp = System.currentTimeMillis()
    }

    fun clear() {
        cachedData = null
        timestamp = 0L
    }
}
