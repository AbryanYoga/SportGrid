package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.mapper.toDomain
import com.abryan.sportgrid.data.mapper.toDomainList
import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.domain.model.League
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.Standing
import com.abryan.sportgrid.util.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

class LeagueRepository {

    private val leagueCache = ConcurrentHashMap<SportType, MemoryCache<List<League>>>()
    private val standingsCache = ConcurrentHashMap<String, MemoryCache<List<Standing>>>()

    suspend fun getPopularLeagues(
        sportType: SportType,
        forceRefresh: Boolean = false
    ): Result<List<League>> = withContext(Dispatchers.IO) {
        val cache = leagueCache.getOrPut(sportType) { MemoryCache(AppConfig.TTL_LEAGUES_MS) }

        if (!forceRefresh) {
            val cached = cache.get()
            if (cached != null) return@withContext Result.success(cached)
        }

        try {
            val leagues = if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getLeagues(
                    season = AppConfig.DEFAULT_SEASON,
                    current = "true"
                )
                if (response.hasErrors) {
                    val fallback = cache.getEvenIfExpired()
                    if (fallback != null) return@withContext Result.success(fallback)
                    return@withContext Result.failure(Exception("API Error: ${response.errors}"))
                }
                response.response.map { it.toDomain() }
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getLeagues(
                    season = AppConfig.DEFAULT_SEASON_STR
                )
                if (response.hasErrors) {
                    val fallback = cache.getEvenIfExpired()
                    if (fallback != null) return@withContext Result.success(fallback)
                    return@withContext Result.failure(Exception("API Error: ${response.errors}"))
                }
                response.response.map { it.toDomain(sportType) }
            }

            cache.put(leagues)
            if (leagues.isNotEmpty()) Result.success(leagues)
            else Result.success(com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackLeagues(sportType))
        } catch (e: Exception) {
            val fallback = cache.getEvenIfExpired()
            if (fallback != null && fallback.isNotEmpty()) Result.success(fallback)
            else Result.success(com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackLeagues(sportType))
        }
    }

    suspend fun searchLeagues(
        sportType: SportType,
        query: String
    ): Result<List<League>> = withContext(Dispatchers.IO) {
        try {
            val leagues = if (sportType.isFootball) {
                ApiClientFactory.getFootballApi().getLeagues(search = query).response.map { it.toDomain() }
            } else {
                ApiClientFactory.getGamesApi(sportType).getLeagues(search = query).response.map { it.toDomain(sportType) }
            }
            Result.success(leagues)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStandings(
        sportType: SportType,
        leagueId: Int
    ): Result<List<Standing>> = withContext(Dispatchers.IO) {
        val cacheKey = "${sportType.slug}_$leagueId"
        val cache = standingsCache.getOrPut(cacheKey) { MemoryCache(AppConfig.TTL_LEAGUES_MS) }

        val cached = cache.get()
        if (cached != null) return@withContext Result.success(cached)

        try {
            val standings = if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getStandings(
                    league = leagueId,
                    season = AppConfig.DEFAULT_SEASON
                )
                response.response.firstOrNull()?.toDomainList() ?: emptyList()
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getStandings(
                    league = leagueId,
                    season = AppConfig.DEFAULT_SEASON_STR
                )
                response.response.map { it.toDomain() }
            }
            cache.put(standings)
            Result.success(standings)
        } catch (e: Exception) {
            val fallback = cache.getEvenIfExpired()
            if (fallback != null) Result.success(fallback) else Result.failure(e)
        }
    }
}
