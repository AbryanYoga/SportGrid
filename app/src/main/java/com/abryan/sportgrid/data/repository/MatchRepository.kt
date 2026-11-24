package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.mapper.toDomain
import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.util.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class MatchRepository {

    private val liveCache = ConcurrentHashMap<SportType, MemoryCache<List<Match>>>()
    private val todayCache = ConcurrentHashMap<SportType, MemoryCache<List<Match>>>()

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }

    suspend fun getLiveMatches(
        sportType: SportType,
        forceRefresh: Boolean = false
    ): Result<List<Match>> = withContext(Dispatchers.IO) {
        val cache = liveCache.getOrPut(sportType) { MemoryCache(AppConfig.TTL_LIVE_MS) }

        if (!forceRefresh) {
            val cached = cache.get()
            if (cached != null) return@withContext Result.success(cached)
        }

        try {
            val matches = if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getFixtures(live = "all")
                response.response.map { it.toDomain() }
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getGames(live = "all")
                response.response.map { it.toDomain(sportType) }
            }
            cache.put(matches)
            Result.success(matches)
        } catch (e: Exception) {
            val fallback = cache.getEvenIfExpired()
            if (fallback != null) Result.success(fallback) else Result.failure(e)
        }
    }

    suspend fun getTodayMatches(
        sportType: SportType,
        forceRefresh: Boolean = false
    ): Result<List<Match>> = withContext(Dispatchers.IO) {
        val cache = todayCache.getOrPut(sportType) { MemoryCache(AppConfig.TTL_MATCHES_MS) }

        if (!forceRefresh) {
            val cached = cache.get()
            if (cached != null) return@withContext Result.success(cached)
        }

        try {
            val todayStr = getTodayDateString()
            val matches = if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getFixtures(date = todayStr)
                response.response.map { it.toDomain() }
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getGames(date = todayStr)
                response.response.map { it.toDomain(sportType) }
            }
            cache.put(matches)
            Result.success(matches)
        } catch (e: Exception) {
            val fallback = cache.getEvenIfExpired()
            if (fallback != null) Result.success(fallback) else Result.failure(e)
        }
    }

    suspend fun getLeagueMatches(
        sportType: SportType,
        leagueId: Int
    ): Result<List<Match>> = withContext(Dispatchers.IO) {
        try {
            val matches = if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getFixtures(
                    league = leagueId,
                    season = AppConfig.DEFAULT_SEASON
                )
                response.response.map { it.toDomain() }
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getGames(
                    league = leagueId,
                    season = AppConfig.DEFAULT_SEASON_STR
                )
                response.response.map { it.toDomain(sportType) }
            }
            Result.success(matches)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
