package com.abryan.sportgrid.data.remote.api

import com.abryan.sportgrid.data.remote.dto.CoachDetailDto
import com.abryan.sportgrid.data.remote.dto.FootballFixtureItemDto
import com.abryan.sportgrid.data.remote.dto.FootballLeagueItemDto
import com.abryan.sportgrid.data.remote.dto.FootballStandingItemDto
import com.abryan.sportgrid.data.remote.dto.FootballTransferItemDto
import com.abryan.sportgrid.data.remote.dto.PlayerProfileItemDto
import com.abryan.sportgrid.data.remote.dto.TeamDetailDto
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {

    @GET("leagues")
    suspend fun getLeagues(
        @Query("season") season: Int? = null,
        @Query("current") current: String? = null,
        @Query("search") search: String? = null
    ): ApiResponse<FootballLeagueItemDto>

    @GET("fixtures")
    suspend fun getFixtures(
        @Query("live") live: String? = null,
        @Query("date") date: String? = null,
        @Query("league") league: Int? = null,
        @Query("season") season: Int? = null
    ): ApiResponse<FootballFixtureItemDto>

    @GET("standings")
    suspend fun getStandings(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): ApiResponse<FootballStandingItemDto>

    @GET("teams")
    suspend fun getTeams(
        @Query("league") league: Int? = null,
        @Query("season") season: Int? = null,
        @Query("search") search: String? = null
    ): ApiResponse<TeamDetailDto>

    @GET("transfers")
    suspend fun getTransfers(
        @Query("team") team: Int
    ): ApiResponse<FootballTransferItemDto>

    @GET("players/profiles")
    suspend fun searchPlayerProfiles(
        @Query("search") search: String
    ): ApiResponse<PlayerProfileItemDto>

    @GET("coachs")
    suspend fun searchCoaches(
        @Query("search") search: String
    ): ApiResponse<CoachDetailDto>
}
