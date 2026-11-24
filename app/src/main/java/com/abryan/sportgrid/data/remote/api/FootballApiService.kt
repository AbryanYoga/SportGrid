package com.abryan.sportgrid.data.remote.api

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {

    @GET("leagues")
    suspend fun getLeagues(
        @Query("season") season: Int? = null,
        @Query("current") current: String? = null,
        @Query("search") search: String? = null
    ): ApiResponse<JsonObject>

    @GET("fixtures")
    suspend fun getFixtures(
        @Query("live") live: String? = null,
        @Query("date") date: String? = null,
        @Query("league") league: Int? = null,
        @Query("season") season: Int? = null
    ): ApiResponse<JsonObject>

    @GET("standings")
    suspend fun getStandings(
        @Query("league") league: Int,
        @Query("season") season: Int
    ): ApiResponse<JsonObject>

    @GET("teams")
    suspend fun getTeams(
        @Query("league") league: Int? = null,
        @Query("season") season: Int? = null,
        @Query("search") search: String? = null
    ): ApiResponse<JsonObject>

    @GET("transfers")
    suspend fun getTransfers(
        @Query("team") team: Int
    ): ApiResponse<JsonObject>
}
