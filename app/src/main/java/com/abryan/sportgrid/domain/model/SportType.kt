package com.abryan.sportgrid.domain.model

import androidx.annotation.StringRes
import com.abryan.sportgrid.R

enum class SportType(
    val slug: String,
    val baseUrl: String,
    @StringRes val displayNameRes: Int,
    val isFootball: Boolean = false
) {
    FOOTBALL(
        slug = "football",
        baseUrl = "https://v3.football.api-sports.io/",
        displayNameRes = R.string.sport_football,
        isFootball = true
    ),
    BASKETBALL(
        slug = "basketball",
        baseUrl = "https://v1.basketball.api-sports.io/",
        displayNameRes = R.string.sport_basketball
    ),
    VOLLEYBALL(
        slug = "volleyball",
        baseUrl = "https://v1.volleyball.api-sports.io/",
        displayNameRes = R.string.sport_volleyball
    ),
    BASEBALL(
        slug = "baseball",
        baseUrl = "https://v1.baseball.api-sports.io/",
        displayNameRes = R.string.sport_baseball
    ),
    HOCKEY(
        slug = "hockey",
        baseUrl = "https://v1.hockey.api-sports.io/",
        displayNameRes = R.string.sport_hockey
    )
}
