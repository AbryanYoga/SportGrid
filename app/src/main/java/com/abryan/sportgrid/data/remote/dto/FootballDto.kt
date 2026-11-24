package com.abryan.sportgrid.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FootballLeagueItemDto(
    val league: LeagueDetailDto? = null,
    val country: CountryDto? = null
)

@Serializable
data class LeagueDetailDto(
    val id: Int? = null,
    val name: String? = null,
    val type: String? = null,
    val logo: String? = null
)

@Serializable
data class CountryDto(
    val name: String? = null,
    val code: String? = null,
    val flag: String? = null
)

@Serializable
data class FootballFixtureItemDto(
    val fixture: FixtureMetaDto? = null,
    val league: LeagueDetailDto? = null,
    val teams: TeamsPairDto? = null,
    val goals: GoalsDto? = null
)

@Serializable
data class FixtureMetaDto(
    val id: Int? = null,
    val date: String? = null,
    val status: StatusDto? = null
)

@Serializable
data class StatusDto(
    val long: String? = null,
    val short: String? = null,
    val elapsed: Int? = null
)

@Serializable
data class TeamsPairDto(
    val home: TeamDetailDto? = null,
    val away: TeamDetailDto? = null
)

@Serializable
data class TeamDetailDto(
    val id: Int? = null,
    val name: String? = null,
    val logo: String? = null
)

@Serializable
data class GoalsDto(
    val home: Int? = null,
    val away: Int? = null
)

@Serializable
data class FootballStandingItemDto(
    val league: StandingsLeagueDto? = null
)

@Serializable
data class StandingsLeagueDto(
    val id: Int? = null,
    val name: String? = null,
    val standings: List<List<StandingRowDto>> = emptyList()
)

@Serializable
data class StandingRowDto(
    val rank: Int? = null,
    val team: TeamDetailDto? = null,
    val points: Int? = null,
    val all: RecordDto? = null
)

@Serializable
data class RecordDto(
    val played: Int? = null,
    val win: Int? = null,
    val draw: Int? = null,
    val lose: Int? = null
)

@Serializable
data class FootballTransferItemDto(
    val player: PlayerDto? = null,
    val transfers: List<TransferEntryDto> = emptyList()
)

@Serializable
data class PlayerDto(
    val id: Int? = null,
    val name: String? = null
)

@Serializable
data class TransferEntryDto(
    val date: String? = null,
    val type: String? = null,
    val teams: TransferTeamsPairDto? = null
)

@Serializable
data class TransferTeamsPairDto(
    val `in`: TeamDetailDto? = null,
    val `out`: TeamDetailDto? = null
)
