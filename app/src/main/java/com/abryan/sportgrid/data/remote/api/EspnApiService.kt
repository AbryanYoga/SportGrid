package com.abryan.sportgrid.data.remote.api

import com.abryan.sportgrid.data.remote.dto.EspnScoreboardResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface EspnApiService {

    @GET("apis/site/v2/sports/{sport}/{league}/scoreboard")
    suspend fun getScoreboard(
        @Path("sport") sport: String,
        @Path("league") league: String,
        @Query("dates") dates: String? = null
    ): EspnScoreboardResponse
}
