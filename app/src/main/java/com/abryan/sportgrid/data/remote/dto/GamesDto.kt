package com.abryan.sportgrid.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GamesLeagueItemDto(
    val id: Int? = null,
    val name: String? = null,
    val type: String? = null,
    val logo: String? = null,
    val country: CountryDto? = null
)

@Serializable
data class GamesMatchItemDto(
    val id: Int? = null,
    val date: String? = null,
    val time: String? = null,
    val status: GamesStatusDto? = null,
    val league: GamesLeagueSimpleDto? = null,
    val teams: GamesTeamsPairDto? = null,
    val scores: GamesScoresDto? = null
)

@Serializable
data class GamesStatusDto(
    val long: String? = null,
    val short: String? = null,
    val timer: String? = null
)

@Serializable
data class GamesLeagueSimpleDto(
    val id: Int? = null,
    val name: String? = null,
    val logo: String? = null
)

@Serializable
data class GamesTeamsPairDto(
    val home: GamesTeamDetailDto? = null,
    val away: GamesTeamDetailDto? = null
)

@Serializable
data class GamesTeamDetailDto(
    val id: Int? = null,
    val name: String? = null,
    val logo: String? = null
)

@Serializable
data class GamesScoresDto(
    val home: GamesScoreTotalDto? = null,
    val away: GamesScoreTotalDto? = null
)

@Serializable
data class GamesScoreTotalDto(
    val total: Int? = null
)

@Serializable
data class GamesStandingItemDto(
    val position: Int? = null,
    val team: GamesTeamDetailDto? = null,
    val games: GamesWinLossDto? = null,
    val points: GamesPointsDto? = null
)

@Serializable
data class GamesWinLossDto(
    val played: Int? = null,
    val win: GamesScoreTotalDto? = null,
    val lose: GamesScoreTotalDto? = null
)

@Serializable
data class GamesPointsDto(
    val `for`: Int? = null,
    val against: Int? = null
)
