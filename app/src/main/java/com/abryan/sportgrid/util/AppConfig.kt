package com.abryan.sportgrid.util

object AppConfig {
    // API-Sports Free tier hanya mengizinkan musim 2022 hingga 2024 ("Free plans do not have access to this season, try from 2022 to 2024.")
    const val API_SPORTS_SEASON = 2024
    const val API_SPORTS_SEASON_STR = "2024-2025"

    const val DEFAULT_SEASON = 2024
    const val DEFAULT_SEASON_STR = "2024-2025"

    const val API_TIMEOUT_SECONDS = 30L

    // Cache TTL durations
    const val TTL_LEAGUES_MS = 24 * 60 * 60 * 1000L   // 24 hours
    const val TTL_MATCHES_MS = 5 * 60 * 1000L          // 5 minutes
    const val TTL_LIVE_MS = 60 * 1000L                 // 60 seconds
    const val TTL_TRANSFERS_MS = 6 * 60 * 60 * 1000L   // 6 hours
}
