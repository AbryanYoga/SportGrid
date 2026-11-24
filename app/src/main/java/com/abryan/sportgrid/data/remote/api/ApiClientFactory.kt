package com.abryan.sportgrid.data.remote.api

import com.abryan.sportgrid.BuildConfig
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.util.AppConfig
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

class HeaderInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("x-apisports-key", apiKey)
            .build()
        return chain.proceed(request)
    }
}

object ApiClientFactory {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private fun createOkHttpClient(apiKey: String): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor(apiKey))
            .connectTimeout(AppConfig.API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(AppConfig.API_TIMEOUT_SECONDS, TimeUnit.SECONDS)

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        builder.addInterceptor(logging)

        return builder.build()
    }

    private val clientCache = mutableMapOf<SportType, Retrofit>()

    fun getRetrofit(sportType: SportType, apiKey: String = BuildConfig.API_SPORTS_KEY): Retrofit {
        return clientCache.getOrPut(sportType) {
            val contentType = "application/json".toMediaType()
            Retrofit.Builder()
                .baseUrl(sportType.baseUrl)
                .client(createOkHttpClient(apiKey))
                .addConverterFactory(json.asConverterFactory(contentType))
                .build()
        }
    }

    fun getFootballApi(apiKey: String = BuildConfig.API_SPORTS_KEY): FootballApiService {
        return getRetrofit(SportType.FOOTBALL, apiKey).create(FootballApiService::class.java)
    }

    fun getGamesApi(sportType: SportType, apiKey: String = BuildConfig.API_SPORTS_KEY): GamesApiService {
        return getRetrofit(sportType, apiKey).create(GamesApiService::class.java)
    }
}
