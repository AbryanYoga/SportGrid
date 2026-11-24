package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.mapper.toDomainList
import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.data.remote.dto.TeamDetailDto
import com.abryan.sportgrid.domain.model.Transfer
import com.abryan.sportgrid.util.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

data class ClubOption(
    val id: Int,
    val name: String,
    val logo: String?
)

class TransferRepository {

    private val transferCache = ConcurrentHashMap<Int, MemoryCache<List<Transfer>>>()
    private val recentTransferCache = MemoryCache<List<Transfer>>(AppConfig.TTL_TRANSFERS_MS)

    val defaultPopularClubs = listOf(
        ClubOption(541, "Real Madrid", "https://media.api-sports.io/football/teams/541.png"),
        ClubOption(42, "Arsenal", "https://media.api-sports.io/football/teams/42.png"),
        ClubOption(529, "Barcelona", "https://media.api-sports.io/football/teams/529.png"),
        ClubOption(50, "Manchester City", "https://media.api-sports.io/football/teams/50.png"),
        ClubOption(40, "Liverpool", "https://media.api-sports.io/football/teams/40.png"),
        ClubOption(157, "Bayern Munich", "https://media.api-sports.io/football/teams/157.png"),
        ClubOption(496, "Juventus", "https://media.api-sports.io/football/teams/496.png"),
        ClubOption(85, "Paris Saint Germain", "https://media.api-sports.io/football/teams/85.png")
    )

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
                .sortedByDescending { it.date }
            cache.put(transfers)
            Result.success(transfers)
        } catch (e: Exception) {
            val fallback = cache.getEvenIfExpired()
            if (fallback != null && fallback.isNotEmpty()) Result.success(fallback)
            else Result.success(com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackTransfers().sortedByDescending { it.date })
        }
    }

    suspend fun searchTeams(query: String): Result<List<ClubOption>> = withContext(Dispatchers.IO) {
        try {
            val response = ApiClientFactory.getFootballApi().getTeams(search = query)
            val clubs = response.response.mapNotNull { item ->
                val t = item.team ?: return@mapNotNull null
                ClubOption(id = t.id ?: 0, name = t.name ?: "Unknown", logo = t.logo)
            }
            Result.success(clubs)
        } catch (e: Exception) {
            val localMatches = defaultPopularClubs.filter { it.name.contains(query, ignoreCase = true) }
            Result.success(localMatches)
        }
    }

    suspend fun getRecentTransfers(forceRefresh: Boolean = false): Result<List<Transfer>> = withContext(Dispatchers.IO) {
        if (!forceRefresh) {
            val cached = recentTransferCache.get()
            if (cached != null) return@withContext Result.success(cached)
        }

        val sampleTeamId = defaultPopularClubs.first().id
        val result = getTransfersByTeam(sampleTeamId, forceRefresh)
        if (result.isSuccess) {
            val list = result.getOrNull().orEmpty()
            recentTransferCache.put(list)
            Result.success(list)
        } else {
            val fallback = recentTransferCache.getEvenIfExpired()
            if (fallback != null) Result.success(fallback) else result
        }
    }
}
