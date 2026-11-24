package com.abryan.sportgrid.data.mapper

import com.abryan.sportgrid.data.remote.dto.FootballFixtureItemDto
import com.abryan.sportgrid.data.remote.dto.FootballLeagueItemDto
import com.abryan.sportgrid.data.remote.dto.FootballStandingItemDto
import com.abryan.sportgrid.data.remote.dto.FootballTransferItemDto
import com.abryan.sportgrid.data.remote.dto.GamesLeagueItemDto
import com.abryan.sportgrid.data.remote.dto.GamesMatchItemDto
import com.abryan.sportgrid.data.remote.dto.GamesStandingItemDto
import com.abryan.sportgrid.domain.model.League
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchStatus
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.Standing
import com.abryan.sportgrid.domain.model.Transfer

fun FootballLeagueItemDto.toDomain(): League {
    return League(
        id = league?.id ?: 0,
        name = league?.name ?: "Unknown League",
        logo = league?.logo,
        countryName = country?.name,
        countryFlag = country?.flag,
        type = league?.type ?: "League",
        sportType = SportType.FOOTBALL
    )
}

fun GamesLeagueItemDto.toDomain(sportType: SportType): League {
    return League(
        id = id ?: 0,
        name = name ?: "Unknown League",
        logo = logo,
        countryName = country?.name,
        countryFlag = country?.flag,
        type = type ?: "League",
        sportType = sportType
    )
}

fun FootballFixtureItemDto.toDomain(): Match {
    val statusShort = fixture?.status?.short ?: "NS"
    val matchStatus = when (statusShort) {
        "1H", "2H", "ET", "P", "LIVE" -> MatchStatus.LIVE
        "FT", "AET", "PEN" -> MatchStatus.FINISHED
        "PST", "CANC", "ABD" -> MatchStatus.POSTPONED
        else -> MatchStatus.NOT_STARTED
    }

    return Match(
        id = fixture?.id ?: 0,
        sportType = SportType.FOOTBALL,
        leagueName = league?.name ?: "Football",
        leagueLogo = league?.logo,
        homeTeamName = teams?.home?.name ?: "Home",
        homeTeamLogo = teams?.home?.logo,
        awayTeamName = teams?.away?.name ?: "Away",
        awayTeamLogo = teams?.away?.logo,
        homeScore = goals?.home,
        awayScore = goals?.away,
        status = matchStatus,
        statusText = statusShort,
        elapsedMinutes = fixture?.status?.elapsed,
        matchDate = fixture?.date ?: ""
    )
}

fun GamesMatchItemDto.toDomain(sportType: SportType): Match {
    val statusShort = status?.short ?: "NS"
    val matchStatus = when (statusShort) {
        "Q1", "Q2", "Q3", "Q4", "OT", "LIVE", "S1", "S2", "S3", "S4", "S5", "IN1", "IN2", "IN3", "IN4", "IN5", "IN6", "IN7", "IN8", "IN9" -> MatchStatus.LIVE
        "FT", "AOT", "AP" -> MatchStatus.FINISHED
        "POST", "CANC" -> MatchStatus.POSTPONED
        else -> MatchStatus.NOT_STARTED
    }

    return Match(
        id = id ?: 0,
        sportType = sportType,
        leagueName = league?.name ?: sportType.name,
        leagueLogo = league?.logo,
        homeTeamName = teams?.home?.name ?: "Home",
        homeTeamLogo = teams?.home?.logo,
        awayTeamName = teams?.away?.name ?: "Away",
        awayTeamLogo = teams?.away?.logo,
        homeScore = scores?.home?.total,
        awayScore = scores?.away?.total,
        status = matchStatus,
        statusText = statusShort,
        elapsedMinutes = status?.timer?.filter { it.isDigit() }?.toIntOrNull(),
        matchDate = date ?: ""
    )
}

fun FootballStandingItemDto.toDomainList(): List<Standing> {
    val rows = league?.standings?.firstOrNull() ?: emptyList()
    return rows.map { row ->
        Standing(
            rank = row.rank ?: 0,
            teamName = row.team?.name ?: "Unknown",
            teamLogo = row.team?.logo,
            played = row.all?.played ?: 0,
            won = row.all?.win ?: 0,
            drawn = row.all?.draw ?: 0,
            lost = row.all?.lose ?: 0,
            points = row.points ?: 0
        )
    }
}

fun GamesStandingItemDto.toDomain(): Standing {
    return Standing(
        rank = position ?: 0,
        teamName = team?.name ?: "Unknown",
        teamLogo = team?.logo,
        played = games?.played ?: 0,
        won = games?.win?.total ?: 0,
        drawn = 0,
        lost = games?.lose?.total ?: 0,
        points = points?.`for` ?: 0
    )
}

fun FootballTransferItemDto.toDomainList(): List<Transfer> {
    val pName = player?.name ?: "Player"
    val pPhoto = player?.id?.let { "https://media.api-sports.io/football/players/$it.png" }
    return transfers.mapIndexed { idx, t ->
        Transfer(
            id = "${player?.id ?: idx}_${t.date ?: idx}",
            playerName = pName,
            playerPhoto = pPhoto,
            fromTeamName = t.teams?.`out`?.name ?: "Previous Club",
            fromTeamLogo = t.teams?.`out`?.logo,
            toTeamName = t.teams?.`in`?.name ?: "New Club",
            toTeamLogo = t.teams?.`in`?.logo,
            type = t.type ?: "Transfer",
            date = t.date ?: ""
        )
    }
}
