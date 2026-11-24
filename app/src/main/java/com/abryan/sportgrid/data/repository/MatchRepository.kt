package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.mapper.toDomain
import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.data.remote.dto.toDomain
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchStatus
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
            if (cached != null && cached.isNotEmpty()) return@withContext Result.success(cached)
        }

        // Sumber 1: ESPN Public API (Live Scores bebas kuota & update detik demi detik)
        val espnMatches = fetchFromEspn(sportType)
        val espnLive = espnMatches?.filter { it.status == MatchStatus.LIVE }
        if (!espnLive.isNullOrEmpty()) {
            cache.put(espnLive)
            return@withContext Result.success(espnLive)
        }

        // Sumber 2: API-Sports (Football / Games live endpoint)
        val apiSportsLive = fetchFromApiSportsLive(sportType)
        if (!apiSportsLive.isNullOrEmpty()) {
            cache.put(apiSportsLive)
            return@withContext Result.success(apiSportsLive)
        }

        // Sumber 3: Football-Data.org API (Khusus Sepak Bola)
        if (sportType.isFootball) {
            val footballDataLive = fetchFromFootballData(status = "LIVE")
            if (!footballDataLive.isNullOrEmpty()) {
                cache.put(footballDataLive)
                return@withContext Result.success(footballDataLive)
            }
        }

        // Jika tidak ada pertandingan live (misal siang hari), tampilkan pertandingan hari ini dari ESPN atau API-Sports
        if (!espnMatches.isNullOrEmpty()) {
            cache.put(espnMatches)
            return@withContext Result.success(espnMatches)
        }

        // Terakhir: Cache lokal atau Smart Fallback Data
        val fallback = cache.getEvenIfExpired()
        if (fallback != null && fallback.isNotEmpty()) Result.success(fallback)
        else Result.success(com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackLiveMatches(sportType))
    }

    suspend fun getTodayMatches(
        sportType: SportType,
        forceRefresh: Boolean = false
    ): Result<List<Match>> = withContext(Dispatchers.IO) {
        val cache = todayCache.getOrPut(sportType) { MemoryCache(AppConfig.TTL_MATCHES_MS) }

        if (!forceRefresh) {
            val cached = cache.get()
            if (cached != null && cached.isNotEmpty()) return@withContext Result.success(cached)
        }

        // Sumber 1: ESPN Public API (Jadwal komprehensif hari ini)
        val espnMatches = fetchFromEspn(sportType)
        if (!espnMatches.isNullOrEmpty()) {
            cache.put(espnMatches)
            return@withContext Result.success(espnMatches)
        }

        // Sumber 2: API-Sports (Fixtures berdasarkan tanggal hari ini)
        val todayStr = getTodayDateString()
        val apiSportsToday = fetchFromApiSportsToday(sportType, todayStr)
        if (!apiSportsToday.isNullOrEmpty()) {
            cache.put(apiSportsToday)
            return@withContext Result.success(apiSportsToday)
        }

        // Sumber 3: Football-Data.org API (Khusus Sepak Bola)
        if (sportType.isFootball) {
            val footballDataMatches = fetchFromFootballData(date = todayStr)
            if (!footballDataMatches.isNullOrEmpty()) {
                cache.put(footballDataMatches)
                return@withContext Result.success(footballDataMatches)
            }
        }

        // Terakhir: Cache lokal atau Smart Fallback Data
        val fallback = cache.getEvenIfExpired()
        if (fallback != null && fallback.isNotEmpty()) Result.success(fallback)
        else Result.success(com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackTodayMatches(sportType))
    }

    suspend fun getLeagueMatches(
        sportType: SportType,
        leagueId: Int
    ): Result<List<Match>> = withContext(Dispatchers.IO) {
        try {
            val matches = if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getFixtures(
                    league = leagueId,
                    season = AppConfig.API_SPORTS_SEASON
                )
                response.response.map { it.toDomain() }
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getGames(
                    league = leagueId,
                    season = AppConfig.API_SPORTS_SEASON_STR
                )
                response.response.map { it.toDomain(sportType) }
            }
            if (matches.isNotEmpty()) Result.success(matches)
            else Result.success(com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackLiveMatches(sportType))
        } catch (e: Exception) {
            Result.success(com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackLiveMatches(sportType))
        }
    }

    // --- Helper Methods untuk Multi-Source API ---

    private suspend fun fetchFromEspn(sportType: SportType): List<Match>? {
        return try {
            val (sport, league, defaultName) = when (sportType) {
                SportType.FOOTBALL -> Triple("soccer", "eng.1", "Premier League")
                SportType.BASKETBALL -> Triple("basketball", "nba", "NBA")
                SportType.BASEBALL -> Triple("baseball", "mlb", "MLB")
                SportType.HOCKEY -> Triple("hockey", "nhl", "NHL")
                SportType.VOLLEYBALL -> return null // ESPN tidak memiliki scoreboard voli publik
            }
            val response = ApiClientFactory.getEspnApi().getScoreboard(sport, league)
            val leagueName = response.leagues?.firstOrNull()?.name ?: defaultName
            response.events?.mapNotNull { it.toDomain(sportType, leagueName) }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchFromApiSportsLive(sportType: SportType): List<Match>? {
        return try {
            if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getFixtures(live = "all")
                response.response.map { it.toDomain() }
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getGames(live = "all")
                response.response.map { it.toDomain(sportType) }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchFromApiSportsToday(sportType: SportType, date: String): List<Match>? {
        return try {
            if (sportType.isFootball) {
                val response = ApiClientFactory.getFootballApi().getFixtures(date = date)
                response.response.map { it.toDomain() }
            } else {
                val response = ApiClientFactory.getGamesApi(sportType).getGames(date = date)
                response.response.map { it.toDomain(sportType) }
            }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun fetchFromFootballData(date: String? = null, status: String? = null): List<Match>? {
        return try {
            val response = ApiClientFactory.getFootballDataApi().getMatches(
                dateFrom = date,
                dateTo = date,
                status = status
            )
            response.matches?.map { it.toDomain() }
        } catch (e: Exception) {
            null
        }
    }
}
