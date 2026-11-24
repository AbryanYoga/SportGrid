package com.abryan.sportgrid.data.remote.dto

import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchStatus
import com.abryan.sportgrid.domain.model.SportType
import kotlinx.serialization.Serializable

@Serializable
data class EspnScoreboardResponse(
    val leagues: List<EspnLeagueDto>? = null,
    val events: List<EspnEventDto>? = null
)

@Serializable
data class EspnLeagueDto(
    val id: String? = null,
    val name: String? = null,
    val abbreviation: String? = null,
    val logos: List<EspnLogoDto>? = null
)

@Serializable
data class EspnLogoDto(
    val href: String? = null
)

@Serializable
data class EspnEventDto(
    val id: String? = null,
    val date: String? = null,
    val name: String? = null,
    val shortName: String? = null,
    val competitions: List<EspnCompetitionDto>? = null
)

@Serializable
data class EspnCompetitionDto(
    val id: String? = null,
    val date: String? = null,
    val competitors: List<EspnCompetitorDto>? = null,
    val status: EspnStatusDto? = null
)

@Serializable
data class EspnCompetitorDto(
    val id: String? = null,
    val homeAway: String? = null,
    val score: String? = null,
    val team: EspnTeamDto? = null
)

@Serializable
data class EspnTeamDto(
    val id: String? = null,
    val name: String? = null,
    val displayName: String? = null,
    val shortDisplayName: String? = null,
    val abbreviation: String? = null,
    val logo: String? = null
)

@Serializable
data class EspnStatusDto(
    val clock: Double? = null,
    val displayClock: String? = null,
    val period: Int? = null,
    val type: EspnStatusTypeDto? = null
)

@Serializable
data class EspnStatusTypeDto(
    val id: String? = null,
    val name: String? = null,
    val state: String? = null,
    val completed: Boolean? = null,
    val description: String? = null,
    val detail: String? = null,
    val shortDetail: String? = null
)

fun EspnEventDto.toDomain(sportType: SportType, defaultLeagueName: String = ""): Match? {
    val comp = competitions?.firstOrNull() ?: return null
    val homeCompetitor = comp.competitors?.firstOrNull { it.homeAway == "home" }
        ?: comp.competitors?.firstOrNull()
    val awayCompetitor = comp.competitors?.firstOrNull { it.homeAway == "away" }
        ?: comp.competitors?.getOrNull(1)

    val homeTeamName = homeCompetitor?.team?.displayName ?: homeCompetitor?.team?.name ?: "Home"
    val awayTeamName = awayCompetitor?.team?.displayName ?: awayCompetitor?.team?.name ?: "Away"

    val homeScore = homeCompetitor?.score?.toIntOrNull()
    val awayScore = awayCompetitor?.score?.toIntOrNull()

    val statusType = comp.status?.type
    val statusState = statusType?.state?.lowercase()
    val status = when {
        statusState == "in" -> MatchStatus.LIVE
        statusState == "post" || statusType?.completed == true -> MatchStatus.FINISHED
        statusState == "pre" -> MatchStatus.NOT_STARTED
        else -> MatchStatus.NOT_STARTED
    }

    val statusText = when (status) {
        MatchStatus.LIVE -> comp.status?.displayClock?.ifEmpty { "LIVE" } ?: statusType?.shortDetail ?: "LIVE"
        MatchStatus.FINISHED -> statusType?.shortDetail ?: "FT"
        MatchStatus.NOT_STARTED -> statusType?.shortDetail ?: "Upcoming"
        MatchStatus.POSTPONED -> "PST"
    }

    val elapsed = comp.status?.clock?.toInt() ?: comp.status?.period?.let { it * 15 }

    val rawId = id?.filter { it.isDigit() }?.takeLast(8)?.toIntOrNull() ?: System.currentTimeMillis().toInt()

    return Match(
        id = rawId,
        sportType = sportType,
        leagueName = defaultLeagueName.ifEmpty { "ESPN Sports" },
        leagueLogo = null,
        homeTeamName = homeTeamName,
        homeTeamLogo = homeCompetitor?.team?.logo,
        awayTeamName = awayTeamName,
        awayTeamLogo = awayCompetitor?.team?.logo,
        homeScore = homeScore,
        awayScore = awayScore,
        status = status,
        statusText = statusText,
        elapsedMinutes = elapsed,
        matchDate = date?.take(10) ?: ""
    )
}
