package com.abryan.sportgrid

import android.app.Application
import com.abryan.sportgrid.data.repository.LeagueRepository
import com.abryan.sportgrid.data.repository.MatchRepository
import com.abryan.sportgrid.data.repository.PersonRepository
import com.abryan.sportgrid.data.repository.TransferRepository

class AppContainer {
    val leagueRepository by lazy { LeagueRepository() }
    val matchRepository by lazy { MatchRepository() }
    val transferRepository by lazy { TransferRepository() }
    val personRepository by lazy { PersonRepository() }
}

class SportGridApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}
