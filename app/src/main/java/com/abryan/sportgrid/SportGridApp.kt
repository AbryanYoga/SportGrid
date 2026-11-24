package com.abryan.sportgrid

import android.app.Application
import com.abryan.sportgrid.data.local.FavoritesDataStore
import com.abryan.sportgrid.data.repository.ClubRepository
import com.abryan.sportgrid.data.repository.LeagueRepository
import com.abryan.sportgrid.data.repository.MatchRepository
import com.abryan.sportgrid.data.repository.PersonRepository
import com.abryan.sportgrid.data.repository.TransferRepository

class AppContainer(val application: Application) {
    val leagueRepository by lazy { LeagueRepository() }
    val matchRepository by lazy { MatchRepository() }
    val transferRepository by lazy { TransferRepository() }
    val personRepository by lazy { PersonRepository() }
    val clubRepository by lazy { ClubRepository() }
    val favoritesDataStore by lazy { FavoritesDataStore(application) }
}

class SportGridApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
