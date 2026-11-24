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

enum class PersonRole {
    ALL,
    PLAYER,
    COACH,
    CLUB
}

data class CareerItem(
    val teamName: String,
    val teamLogo: String? = null,
    val startYear: String? = null,
    val endYear: String? = null,
    val roleTitle: String? = null
)

data class PersonProfile(
    val id: String,
    val name: String,
    val role: PersonRole,
    val sportType: SportType,
    val photo: String? = null,
    val age: Int? = null,
    val nationality: String? = null,
    val teamName: String? = null,
    val teamLogo: String? = null,
    val number: Int? = null,
    val position: String? = null,
    val height: String? = null,
    val weight: String? = null,
    val careerSummary: String? = null,
    val careerHistory: List<CareerItem> = emptyList(),
    val achievements: List<String> = emptyList(),
    val coachingStyle: String? = null,
    val dominantFootHand: String? = null,
    val marketValue: String? = null
)

data class LineupPlayer(
    val number: Int,
    val name: String,
    val position: String,
    val isGoalkeeper: Boolean = false
)

data class TeamTactics(
    val formation: String,
    val managerName: String,
    val tacticalStyle: String,
    val starting11: List<LineupPlayer> = emptyList()
)

data class MatchStatItem(
    val label: String,
    val homeValue: String,
    val awayValue: String,
    val homeNumeric: Float,
    val awayNumeric: Float
)

data class MatchEvent(
    val minute: Int,
    val type: String, // "GOAL", "CARD", "SUB"
    val player: String,
    val isHomeTeam: Boolean,
    val detail: String? = null
)

data class MatchDetailData(
    val match: Match,
    val homeTactics: TeamTactics,
    val awayTactics: TeamTactics,
    val statistics: List<MatchStatItem>,
    val events: List<MatchEvent> = emptyList(),
    val stadium: String? = null,
    val referee: String? = null
)

data class ClubBio(
    val id: Int,
    val name: String,
    val code: String? = null,
    val logo: String? = null,
    val country: String? = null,
    val foundedYear: Int? = null,
    val president: String? = null,
    val manager: String? = null,
    val stadiumName: String? = null,
    val stadiumCity: String? = null,
    val stadiumCapacity: Int? = null,
    val stadiumImage: String? = null,
    val website: String? = null,
    val trophiesSummary: String? = null,
    val description: String? = null,
    val sportType: SportType = SportType.FOOTBALL
)
