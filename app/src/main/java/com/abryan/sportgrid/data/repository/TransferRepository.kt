package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.mapper.toDomainList
import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.domain.model.Transfer
import com.abryan.sportgrid.util.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class TransferRepository {

    private val transferCache = ConcurrentHashMap<Int, MemoryCache<List<Transfer>>>()
    private val recentTransferCache = MemoryCache<List<Transfer>>(AppConfig.TTL_TRANSFERS_MS)

    // Top clubs (Real Madrid, Arsenal, Barcelona, Man City) for default dashboard preview
    private val popularTeamIds = listOf(541, 42, 529, 50)

    suspend fun getTransfersByTeam(
        teamId: Int,
        forceRefresh: Boolean = false
    ): Result<List<Transfer>> = withContext(Dispatchers.IO) {
        val cache = transferCache.getOrPut(teamId) { MemoryCache(AppConfig.TTL_TRANSFERS_MS) }

        if (!forceRefresh) {
            val cached = cache.get()
            if (cached != null) return@withContext Result.success(cached)
        }

        try {
            val response = ApiClientFactory.getFootballApi().getTransfers(team = teamId)
            val transfers = response.response.flatMap { it.toDomainList() }
            cache.put(transfers)
            Result.success(transfers)
        } catch (e: Exception) {
            val fallback = cache.getEvenIfExpired()
            if (fallback != null) Result.success(fallback) else Result.failure(e)
        }
    }

    suspend fun getRecentTransfers(forceRefresh: Boolean = false): Result<List<Transfer>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val cached = recentTransferCache.get()
            if (cached != null) return@withContext Result.success(cached)
        }

        // Fetch transfers from first popular team to save quota (1 request)
        val sampleTeamId = popularTeamIds.first()
        val result = getTransfersByTeam(sampleTeamId, forceRefresh)
        if (result.isSuccess) {
            val list = result.getOrNull() ?: emptyList()
            recentTransferCache.put(list)
            Result.success(list)
        } else {
            val fallback = recentTransferCache.getEvenIfExpired()
            if (fallback != null) Result.success(fallback) else result
        }
    }
}
