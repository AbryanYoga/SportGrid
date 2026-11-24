package com.abryan.sportgrid.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.favoritesDataStore by preferencesDataStore(name = "sportgrid_favorites")

class FavoritesDataStore(private val context: Context) {

    companion object {
        val KEY_FAVORITE_CLUBS = stringSetPreferencesKey("favorite_clubs")
        val KEY_FAVORITE_PLAYERS = stringSetPreferencesKey("favorite_players")
        val KEY_FAVORITE_MATCHES = stringSetPreferencesKey("favorite_matches")
    }

    val favoriteClubs: Flow<Set<String>> = context.favoritesDataStore.data.map { preferences ->
        preferences[KEY_FAVORITE_CLUBS] ?: emptySet()
    }

    val favoritePlayers: Flow<Set<String>> = context.favoritesDataStore.data.map { preferences ->
        preferences[KEY_FAVORITE_PLAYERS] ?: emptySet()
    }

    val favoriteMatches: Flow<Set<String>> = context.favoritesDataStore.data.map { preferences ->
        preferences[KEY_FAVORITE_MATCHES] ?: emptySet()
    }

    suspend fun toggleFavoriteClub(clubId: Int) {
        val idStr = clubId.toString()
        context.favoritesDataStore.edit { preferences ->
            val current = preferences[KEY_FAVORITE_CLUBS]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(idStr)) {
                current.remove(idStr)
            } else {
                current.add(idStr)
            }
            preferences[KEY_FAVORITE_CLUBS] = current
        }
    }

    suspend fun toggleFavoritePlayer(playerId: String) {
        context.favoritesDataStore.edit { preferences ->
            val current = preferences[KEY_FAVORITE_PLAYERS]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(playerId)) {
                current.remove(playerId)
            } else {
                current.add(playerId)
            }
            preferences[KEY_FAVORITE_PLAYERS] = current
        }
    }

    suspend fun toggleFavoriteMatch(matchId: Int) {
        val idStr = matchId.toString()
        context.favoritesDataStore.edit { preferences ->
            val current = preferences[KEY_FAVORITE_MATCHES]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(idStr)) {
                current.remove(idStr)
            } else {
                current.add(idStr)
            }
            preferences[KEY_FAVORITE_MATCHES] = current
        }
    }
}
