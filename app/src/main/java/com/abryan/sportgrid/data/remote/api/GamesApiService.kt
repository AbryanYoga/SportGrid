package com.abryan.sportgrid.data.remote.api

import kotlinx.serialization.json.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface GamesApiService {

    @GET("leagues")
    suspend fun getLeagues(
        @Query("season") season: String? = null,
        @Query("search") search: String? = null
    ): ApiResponse<JsonObject>

    @GET("games")
    suspend fun getGames(
        @Query("live") live: String? = null,
        @Query("date") date: String? = null,
        @Query("league") league: Int? = null,
        @Query("season") season: String? = null
    ): ApiResponse<JsonObject>

    @GET("standings")
    suspend fun getStandings(
        @Query("league") league: Int,
        @Query("season") season: String? = null
    ): ApiResponse<JsonObject>
}
