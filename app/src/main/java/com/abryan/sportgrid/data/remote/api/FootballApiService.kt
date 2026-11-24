package com.abryan.sportgrid.data.remote.api

import com.abryan.sportgrid.data.remote.dto.FootballFixtureItemDto
import com.abryan.sportgrid.data.remote.dto.FootballLeagueItemDto
import com.abryan.sportgrid.data.remote.dto.FootballStandingItemDto
import com.abryan.sportgrid.data.remote.dto.FootballTransferItemDto
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
}
