package com.abryan.sportgrid.data.remote.dto

import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchStatus
import com.abryan.sportgrid.domain.model.SportType
import kotlinx.serialization.Serializable

@Serializable
data class FootballDataMatchesResponse(
    val matches: List<FootballDataMatchDto>? = null
)

@Serializable
data class FootballDataMatchDto(
    val id: Int? = null,
    val utcDate: String? = null,
    val status: String? = null,
    val competition: FootballDataCompetitionDto? = null,
    val homeTeam: FootballDataTeamDto? = null,
    val awayTeam: FootballDataTeamDto? = null,
    val score: FootballDataScoreDto? = null
)

@Serializable
data class FootballDataCompetitionDto(
    val id: Int? = null,
    val name: String? = null,
    val emblem: String? = null
)

@Serializable
data class FootballDataTeamDto(
    val id: Int? = null,
    val name: String? = null,
    val shortName: String? = null,
    val crest: String? = null
)

@Serializable
data class FootballDataScoreDto(
    val fullTime: FootballDataScoreDetailDto? = null,
    val halfTime: FootballDataScoreDetailDto? = null
)

@Serializable
data class FootballDataScoreDetailDto(
    val home: Int? = null,
    val away: Int? = null
)

fun FootballDataMatchDto.toDomain(): Match {
    val statusEnum = when (status?.uppercase()) {
        "IN_PLAY", "PAUSED", "LIVE" -> MatchStatus.LIVE
        "FINISHED", "AWARDED" -> MatchStatus.FINISHED
        "POSTPONED", "SUSPENDED", "CANCELLED" -> MatchStatus.POSTPONED
        else -> MatchStatus.NOT_STARTED
    }

    val statusText = when (statusEnum) {
        MatchStatus.LIVE -> "LIVE"
        MatchStatus.FINISHED -> "FT"
        MatchStatus.POSTPONED -> "PST"
        MatchStatus.NOT_STARTED -> "Upcoming"
    }

    return Match(
        id = id ?: System.currentTimeMillis().toInt(),
        sportType = SportType.FOOTBALL,
        leagueName = competition?.name ?: "European Football",
        leagueLogo = competition?.emblem,
        homeTeamName = homeTeam?.shortName ?: homeTeam?.name ?: "Home",
        homeTeamLogo = homeTeam?.crest,
        awayTeamName = awayTeam?.shortName ?: awayTeam?.name ?: "Away",
        awayTeamLogo = awayTeam?.crest,
        homeScore = score?.fullTime?.home,
        awayScore = score?.fullTime?.away,
        status = statusEnum,
        statusText = statusText,
        elapsedMinutes = if (statusEnum == MatchStatus.LIVE) 45 else null,
        matchDate = utcDate?.take(10) ?: ""
    )
}
