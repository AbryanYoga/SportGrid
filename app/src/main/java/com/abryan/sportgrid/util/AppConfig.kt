package com.abryan.sportgrid.util

object AppConfig {
    val DEFAULT_SEASON: Int
        get() = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)

    val DEFAULT_SEASON_STR: String
        get() {
            val year = DEFAULT_SEASON
            return "$year-${year + 1}"
        }
    const val API_TIMEOUT_SECONDS = 30L

    // Cache TTL durations
    const val TTL_LEAGUES_MS = 24 * 60 * 60 * 1000L   // 24 hours
    const val TTL_MATCHES_MS = 5 * 60 * 1000L          // 5 minutes
    const val TTL_LIVE_MS = 60 * 1000L                 // 60 seconds
    const val TTL_TRANSFERS_MS = 6 * 60 * 60 * 1000L   // 6 hours
}
