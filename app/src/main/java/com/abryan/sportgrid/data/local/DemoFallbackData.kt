package com.abryan.sportgrid.data.local

import com.abryan.sportgrid.domain.model.League
import com.abryan.sportgrid.domain.model.Match
import com.abryan.sportgrid.domain.model.MatchStatus
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.domain.model.Standing
import com.abryan.sportgrid.domain.model.Transfer

object DemoFallbackData {

    fun getFallbackLeagues(sportType: SportType): List<League> {
        return when (sportType) {
            SportType.FOOTBALL -> listOf(
                League(39, "Premier League", "https://media.api-sports.io/football/leagues/39.png", "England", null, "League", SportType.FOOTBALL),
                League(140, "La Liga", "https://media.api-sports.io/football/leagues/140.png", "Spain", null, "League", SportType.FOOTBALL),
                League(135, "Serie A", "https://media.api-sports.io/football/leagues/135.png", "Italy", null, "League", SportType.FOOTBALL),
                League(78, "Bundesliga", "https://media.api-sports.io/football/leagues/78.png", "Germany", null, "League", SportType.FOOTBALL),
                League(2, "UEFA Champions League", "https://media.api-sports.io/football/leagues/2.png", "World", null, "Cup", SportType.FOOTBALL)
            )
            SportType.BASKETBALL -> listOf(
                League(12, "NBA", "https://media.api-sports.io/basketball/leagues/12.png", "USA", null, "League", SportType.BASKETBALL),
                League(120, "EuroLeague", "https://media.api-sports.io/basketball/leagues/120.png", "Europe", null, "League", SportType.BASKETBALL),
                League(194, "Liga ACB", "https://media.api-sports.io/basketball/leagues/194.png", "Spain", null, "League", SportType.BASKETBALL)
            )
            SportType.VOLLEYBALL -> listOf(
                League(1, "Nations League", "https://media.api-sports.io/volleyball/leagues/1.png", "World", null, "Cup", SportType.VOLLEYBALL),
                League(97, "SuperLega", "https://media.api-sports.io/volleyball/leagues/97.png", "Italy", null, "League", SportType.VOLLEYBALL)
            )
            SportType.BASEBALL -> listOf(
                League(1, "MLB", "https://media.api-sports.io/baseball/leagues/1.png", "USA", null, "League", SportType.BASEBALL),
                League(2, "NPB", "https://media.api-sports.io/baseball/leagues/2.png", "Japan", null, "League", SportType.BASEBALL)
            )
            SportType.HOCKEY -> listOf(
                League(57, "NHL", "https://media.api-sports.io/hockey/leagues/57.png", "USA", null, "League", SportType.HOCKEY),
                League(37, "KHL", "https://media.api-sports.io/hockey/leagues/37.png", "Russia", null, "League", SportType.HOCKEY)
            )
        }
    }

    fun getFallbackLiveMatches(sportType: SportType): List<Match> {
        return when (sportType) {
            SportType.FOOTBALL -> listOf(
                Match(
                    id = 1001,
                    sportType = SportType.FOOTBALL,
                    leagueName = "Premier League",
                    leagueLogo = "https://media.api-sports.io/football/leagues/39.png",
                    homeTeamName = "Arsenal",
                    homeTeamLogo = "https://media.api-sports.io/football/teams/42.png",
                    awayTeamName = "Chelsea",
                    awayTeamLogo = "https://media.api-sports.io/football/teams/49.png",
                    homeScore = 2,
                    awayScore = 1,
                    status = MatchStatus.LIVE,
                    statusText = "68'",
                    elapsedMinutes = 68,
                    matchDate = "2026-11-24"
                )
            )
            SportType.BASKETBALL -> listOf(
                Match(
                    id = 2001,
                    sportType = SportType.BASKETBALL,
                    leagueName = "NBA",
                    leagueLogo = "https://media.api-sports.io/basketball/leagues/12.png",
                    homeTeamName = "OKC Thunder",
                    homeTeamLogo = null,
                    awayTeamName = "Indiana Pacers",
                    awayTeamLogo = null,
                    homeScore = 70,
                    awayScore = 57,
                    status = MatchStatus.LIVE,
                    statusText = "3RD 6:02",
                    elapsedMinutes = 30,
                    matchDate = "2026-11-24"
                ),
                Match(
                    id = 2002,
                    sportType = SportType.BASKETBALL,
                    leagueName = "NBA",
                    leagueLogo = "https://media.api-sports.io/basketball/leagues/12.png",
                    homeTeamName = "LA Lakers",
                    homeTeamLogo = null,
                    awayTeamName = "Golden State",
                    awayTeamLogo = null,
                    homeScore = 76,
                    awayScore = 64,
                    status = MatchStatus.LIVE,
                    statusText = "3RD 9:24",
                    elapsedMinutes = 33,
                    matchDate = "2026-11-24"
                )
            )
            else -> emptyList()
        }
    }

    fun getFallbackTodayMatches(sportType: SportType): List<Match> {
        return when (sportType) {
            SportType.FOOTBALL -> listOf(
                Match(
                    id = 1002,
                    sportType = SportType.FOOTBALL,
                    leagueName = "La Liga",
                    leagueLogo = "https://media.api-sports.io/football/leagues/140.png",
                    homeTeamName = "Real Madrid",
                    homeTeamLogo = "https://media.api-sports.io/football/teams/541.png",
                    awayTeamName = "Barcelona",
                    awayTeamLogo = "https://media.api-sports.io/football/teams/529.png",
                    homeScore = 3,
                    awayScore = 2,
                    status = MatchStatus.FINISHED,
                    statusText = "FT",
                    elapsedMinutes = 90,
                    matchDate = "2026-11-24"
                ),
                Match(
                    id = 1003,
                    sportType = SportType.FOOTBALL,
                    leagueName = "Serie A",
                    leagueLogo = "https://media.api-sports.io/football/leagues/135.png",
                    homeTeamName = "Inter Milan",
                    homeTeamLogo = null,
                    awayTeamName = "AC Milan",
                    awayTeamLogo = null,
                    homeScore = null,
                    awayScore = null,
                    status = MatchStatus.NOT_STARTED,
                    statusText = "20:45",
                    elapsedMinutes = null,
                    matchDate = "2026-11-24"
                )
            )
            SportType.BASKETBALL -> listOf(
                Match(
                    id = 2003,
                    sportType = SportType.BASKETBALL,
                    leagueName = "NBA",
                    leagueLogo = "https://media.api-sports.io/basketball/leagues/12.png",
                    homeTeamName = "Boston Celtics",
                    homeTeamLogo = null,
                    awayTeamName = "Miami Heat",
                    awayTeamLogo = null,
                    homeScore = 112,
                    awayScore = 104,
                    status = MatchStatus.FINISHED,
                    statusText = "FT",
                    elapsedMinutes = 48,
                    matchDate = "2026-11-24"
                )
            )
            SportType.VOLLEYBALL -> listOf(
                Match(
                    id = 3001,
                    sportType = SportType.VOLLEYBALL,
                    leagueName = "SuperLega",
                    leagueLogo = null,
                    homeTeamName = "Perugia",
                    homeTeamLogo = null,
                    awayTeamName = "Trentino",
                    awayTeamLogo = null,
                    homeScore = 3,
                    awayScore = 1,
                    status = MatchStatus.FINISHED,
                    statusText = "FT",
                    elapsedMinutes = null,
                    matchDate = "2026-11-24"
                )
            )
            SportType.BASEBALL -> listOf(
                Match(
                    id = 4001,
                    sportType = SportType.BASEBALL,
                    leagueName = "MLB",
                    leagueLogo = null,
                    homeTeamName = "NY Yankees",
                    homeTeamLogo = null,
                    awayTeamName = "LA Dodgers",
                    awayTeamLogo = null,
                    homeScore = 5,
                    awayScore = 4,
                    status = MatchStatus.FINISHED,
                    statusText = "FT",
                    elapsedMinutes = null,
                    matchDate = "2026-11-24"
                )
            )
            SportType.HOCKEY -> listOf(
                Match(
                    id = 5001,
                    sportType = SportType.HOCKEY,
                    leagueName = "NHL",
                    leagueLogo = null,
                    homeTeamName = "Edmonton Oilers",
                    homeTeamLogo = null,
                    awayTeamName = "Florida Panthers",
                    awayTeamLogo = null,
                    homeScore = 4,
                    awayScore = 2,
                    status = MatchStatus.FINISHED,
                    statusText = "FT",
                    elapsedMinutes = null,
                    matchDate = "2026-11-24"
                )
            )
        }
    }

    fun getFallbackTransfers(): List<Transfer> {
        return listOf(
            Transfer(
                id = "t1",
                playerName = "Kylian Mbappé",
                fromTeamName = "Paris Saint-Germain",
                fromTeamLogo = "https://media.api-sports.io/football/teams/85.png",
                toTeamName = "Real Madrid",
                toTeamLogo = "https://media.api-sports.io/football/teams/541.png",
                type = "Free Transfer",
                date = "2024-07-01"
            ),
            Transfer(
                id = "t2",
                playerName = "Jude Bellingham",
                fromTeamName = "Borussia Dortmund",
                fromTeamLogo = null,
                toTeamName = "Real Madrid",
                toTeamLogo = "https://media.api-sports.io/football/teams/541.png",
                type = "€103M",
                date = "2023-07-01"
            ),
            Transfer(
                id = "t3",
                playerName = "Declan Rice",
                fromTeamName = "West Ham United",
                fromTeamLogo = null,
                toTeamName = "Arsenal",
                toTeamLogo = "https://media.api-sports.io/football/teams/42.png",
                type = "€116M",
                date = "2023-07-15"
            )
        )
    }

    fun getFallbackStandings(): List<Standing> {
        return listOf(
            Standing(1, "Liverpool", "https://media.api-sports.io/football/teams/40.png", 12, 10, 1, 1, 31),
            Standing(2, "Manchester City", "https://media.api-sports.io/football/teams/50.png", 12, 7, 2, 3, 23),
            Standing(3, "Chelsea", "https://media.api-sports.io/football/teams/49.png", 12, 6, 4, 2, 22),
            Standing(4, "Arsenal", "https://media.api-sports.io/football/teams/42.png", 12, 6, 4, 2, 22),
            Standing(5, "Brighton", null, 12, 6, 4, 2, 22)
        )
    }
}
