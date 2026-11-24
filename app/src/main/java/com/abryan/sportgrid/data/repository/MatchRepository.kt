package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.mapper.toDomain
import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.data.remote.dto.toDomain
import com.abryan.sportgrid.domain.model.LineupPlayer
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchDetailData
import com.abryan.sportgrid.domain.model.MatchEvent
import com.abryan.sportgrid.domain.model.MatchStatItem
import com.abryan.sportgrid.domain.model.MatchStatus
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.TeamTactics
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

    suspend fun getMatchDetail(matchId: Int): MatchDetailData? = withContext(Dispatchers.IO) {
        // Cari match dari semua cache yang ada
        var foundMatch: Match? = null
        for (cache in liveCache.values) {
            val list = cache.getEvenIfExpired()
            foundMatch = list?.find { it.id == matchId }
            if (foundMatch != null) break
        }
        if (foundMatch == null) {
            for (cache in todayCache.values) {
                val list = cache.getEvenIfExpired()
                foundMatch = list?.find { it.id == matchId }
                if (foundMatch != null) break
            }
        }
        if (foundMatch == null) {
            val allFallbacks = SportType.entries.flatMap {
                com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackLiveMatches(it) +
                com.abryan.sportgrid.data.local.DemoFallbackData.getFallbackTodayMatches(it)
            }
            foundMatch = allFallbacks.find { it.id == matchId } ?: allFallbacks.firstOrNull()
        }

        if (foundMatch == null) return@withContext null

        val homeName = foundMatch.homeTeamName
        val awayName = foundMatch.awayTeamName

        // Deteksi pelatih dan taktik berdasarkan nama tim
        val (homeManager, homeFormation, homeStyle) = when {
            homeName.contains("Real Madrid", ignoreCase = true) -> Triple("Carlo Ancelotti", "4-3-3", "Transisi Cepat & Kebebasan Kreatif Sayap")
            homeName.contains("Barcelona", ignoreCase = true) -> Triple("Hansi Flick", "4-2-3-1", "Gegenpressing Agresif & Garis Pertahanan Tinggi")
            homeName.contains("Manchester City", ignoreCase = true) -> Triple("Pep Guardiola", "3-2-4-1", "Juego de Posición & Inverted Fullbacks")
            homeName.contains("Arsenal", ignoreCase = true) -> Triple("Mikel Arteta", "4-3-3", "Struktur Blok Kokoh & Dominasi Bola Mati")
            homeName.contains("Liverpool", ignoreCase = true) -> Triple("Arne Slot", "4-2-3-1", "Control Possession & Fluid Direct Attacking")
            homeName.contains("Warriors", ignoreCase = true) -> Triple("Steve Kerr", "Pace & Space", "Motion Offense & Tembakan 3 Poin Dinamis")
            homeName.contains("Lakers", ignoreCase = true) -> Triple("JJ Redick", "5-Out Spread", "Pick-and-Roll Dominance & Rim Pressure")
            else -> Triple("Pelatih Kepala Tuan Rumah", "4-3-3", "Serangan Terorganisir & Disiplin Zona")
        }

        val (awayManager, awayFormation, awayStyle) = when {
            awayName.contains("Real Madrid", ignoreCase = true) -> Triple("Carlo Ancelotti", "4-3-3", "Transisi Cepat & Kebebasan Kreatif Sayap")
            awayName.contains("Barcelona", ignoreCase = true) -> Triple("Hansi Flick", "4-2-3-1", "Gegenpressing Agresif & Garis Pertahanan Tinggi")
            awayName.contains("Manchester City", ignoreCase = true) -> Triple("Pep Guardiola", "3-2-4-1", "Juego de Posición & Inverted Fullbacks")
            awayName.contains("Arsenal", ignoreCase = true) -> Triple("Mikel Arteta", "4-3-3", "Struktur Blok Kokoh & Dominasi Bola Mati")
            awayName.contains("Liverpool", ignoreCase = true) -> Triple("Arne Slot", "4-2-3-1", "Control Possession & Fluid Direct Attacking")
            awayName.contains("Warriors", ignoreCase = true) -> Triple("Steve Kerr", "Pace & Space", "Motion Offense & Tembakan 3 Poin Dinamis")
            awayName.contains("Lakers", ignoreCase = true) -> Triple("JJ Redick", "5-Out Spread", "Pick-and-Roll Dominance & Rim Pressure")
            else -> Triple("Pelatih Kepala Tim Tamu", "4-2-3-1", "Counter-Attack Kompak & Blok Rendah Menengah")
        }

        val homeLineup = listOf(
            LineupPlayer(1, "Thibaut Courtois", "GK", isGoalkeeper = true),
            LineupPlayer(2, "Dani Carvajal", "RB"),
            LineupPlayer(3, "Éder Militão", "CB"),
            LineupPlayer(22, "Antonio Rüdiger", "CB"),
            LineupPlayer(23, "Ferland Mendy", "LB"),
            LineupPlayer(14, "Aurélien Tchouaméni", "DM"),
            LineupPlayer(8, "Federico Valverde", "CM"),
            LineupPlayer(5, "Jude Bellingham", "AM"),
            LineupPlayer(11, "Rodrygo", "RW"),
            LineupPlayer(7, "Vinícius Júnior", "LW"),
            LineupPlayer(9, "Kylian Mbappé", "CF")
        )

        val awayLineup = listOf(
            LineupPlayer(1, "Marc-André ter Stegen", "GK", isGoalkeeper = true),
            LineupPlayer(23, "Jules Koundé", "RB"),
            LineupPlayer(2, "Pau Cubarsí", "CB"),
            LineupPlayer(5, "Iñigo Martínez", "CB"),
            LineupPlayer(3, "Alejandro Balde", "LB"),
            LineupPlayer(17, "Marc Casadó", "DM"),
            LineupPlayer(8, "Pedri", "CM"),
            LineupPlayer(20, "Dani Olmo", "AM"),
            LineupPlayer(19, "Lamine Yamal", "RW"),
            LineupPlayer(11, "Raphinha", "LW"),
            LineupPlayer(9, "Robert Lewandowski", "CF")
        )

        val homePossession = if (foundMatch.homeScore != null && foundMatch.awayScore != null && foundMatch.homeScore > foundMatch.awayScore) 56f else 51f
        val awayPossession = 100f - homePossession

        val stats = listOf(
            MatchStatItem("Penguasaan Bola (Possession)", "${homePossession.toInt()}%", "${awayPossession.toInt()}%", homePossession, awayPossession),
            MatchStatItem("Total Tembakan (Total Shots)", "16", "11", 16f, 11f),
            MatchStatItem("Tembakan ke Gawang (On Target)", "7", "4", 7f, 4f),
            MatchStatItem("Akurasi Operan (Pass Accuracy)", "88%", "82%", 88f, 82f),
            MatchStatItem("Pelanggaran (Fouls Committed)", "9", "13", 9f, 13f),
            MatchStatItem("Tendangan Sudut (Corner Kicks)", "6", "4", 6f, 4f),
            MatchStatItem("Kartu Kuning (Yellow Cards)", "1", "3", 1f, 3f),
            MatchStatItem("Kartu Merah (Red Cards)", "0", "0", 0f, 0f),
            MatchStatItem("Offsides", "2", "4", 2f, 4f),
            MatchStatItem("Penyelamatan Kiper (Saves)", "3", "5", 3f, 5f)
        )

        val events = listOf(
            MatchEvent(18, "GOAL", "Kylian Mbappé", true, "Assist oleh Jude Bellingham"),
            MatchEvent(34, "CARD", "Marc Casadó", false, "Pelanggaran taktikal"),
            MatchEvent(52, "GOAL", "Lamine Yamal", false, "Sepakan melengkung kaki kiri"),
            MatchEvent(68, "GOAL", "Jude Bellingham", true, "Sundulan pojok kiri gawang"),
            MatchEvent(75, "SUB", "Rodrygo ➔ Brahim Díaz", true, "Pergantian taktik sayap"),
            MatchEvent(82, "CARD", "Iñigo Martínez", false, "Ditarik wasit karena protes")
        )

        MatchDetailData(
            match = foundMatch,
            homeTactics = TeamTactics(
                formation = homeFormation,
                managerName = homeManager,
                tacticalStyle = homeStyle,
                starting11 = homeLineup
            ),
            awayTactics = TeamTactics(
                formation = awayFormation,
                managerName = awayManager,
                tacticalStyle = awayStyle,
                starting11 = awayLineup
            ),
            statistics = stats,
            events = events,
            stadium = "Estadio Santiago Bernabéu, Madrid",
            referee = "Szymon Marciniak (Wasit FIFA)"
        )
    }
}
