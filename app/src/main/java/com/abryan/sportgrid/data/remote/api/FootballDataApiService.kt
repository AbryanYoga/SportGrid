package com.abryan.sportgrid.data.remote.api

import com.abryan.sportgrid.data.remote.dto.FootballDataMatchesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballDataApiService {

    @GET("v4/matches")
    suspend fun getMatches(
        @Query("dateFrom") dateFrom: String? = null,
        @Query("dateTo") dateTo: String? = null,
        @Query("status") status: String? = null
    ): FootballDataMatchesResponse
}
