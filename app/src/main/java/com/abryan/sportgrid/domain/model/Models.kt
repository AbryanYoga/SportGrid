package com.abryan.sportgrid.domain.model

enum class MatchStatus {
    LIVE,
    FINISHED,
    NOT_STARTED,
    POSTPONED
}

data class League(
    val id: Int,
    val name: String,
    val logo: String?,
    val countryName: String?,
    val countryFlag: String?,
    val type: String?,
    val sportType: SportType
)

data class Match(
    val id: Int,
    val sportType: SportType,
    val leagueName: String,
    val leagueLogo: String?,
    val homeTeamName: String,
    val homeTeamLogo: String?,
    val awayTeamName: String,
    val awayTeamLogo: String?,
    val homeScore: Int?,
    val awayScore: Int?,
    val status: MatchStatus,
    val statusText: String,
    val elapsedMinutes: Int?,
    val matchDate: String
) {
    val isLive: Boolean get() = status == MatchStatus.LIVE
}

data class Transfer(
    val id: String,
    val playerName: String,
    val playerPhoto: String? = null,
    val fromTeamName: String,
    val fromTeamLogo: String?,
    val toTeamName: String,
    val toTeamLogo: String?,
    val type: String,
    val date: String
)

data class Standing(
    val rank: Int,
    val teamName: String,
    val teamLogo: String?,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val points: Int
)
