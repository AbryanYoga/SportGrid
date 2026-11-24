package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.domain.model.ClubBio
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.util.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClubRepository {

    private val clubCache = MemoryCache<List<ClubBio>>(AppConfig.TTL_LEAGUES_MS)

    val topWorldClubs: List<ClubBio> = listOf(
        ClubBio(
            id = 541,
            name = "Real Madrid CF",
            code = "RMA",
            logo = "https://media.api-sports.io/football/teams/541.png",
            country = "Spain",
            foundedYear = 1902,
            president = "Florentino Pérez",
            manager = "Carlo Ancelotti",
            stadiumName = "Estadio Santiago Bernabéu",
            stadiumCity = "Madrid",
            stadiumCapacity = 85454,
            stadiumImage = "https://media.api-sports.io/football/venues/1456.png",
            website = "https://www.realmadrid.com",
            trophiesSummary = "15x Liga Champions Eropa, 36x La Liga, 20x Copa del Rey, 5x Piala Dunia Antarklub FIFA",
            description = "Klub tersukses dalam sejarah sepak bola dunia yang dinobatkan FIFA sebagai Club of the Century abad ke-20. Terkenal dengan era Galácticos dan dominasi Eropa yang tak tertandingi.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 529,
            name = "FC Barcelona",
            code = "BAR",
            logo = "https://media.api-sports.io/football/teams/529.png",
            country = "Spain",
            foundedYear = 1899,
            president = "Joan Laporta",
            manager = "Hansi Flick",
            stadiumName = "Spotify Camp Nou",
            stadiumCity = "Barcelona",
            stadiumCapacity = 99354,
            stadiumImage = "https://media.api-sports.io/football/venues/1492.png",
            website = "https://www.fcbarcelona.com",
            trophiesSummary = "5x Liga Champions Eropa, 27x La Liga, 31x Copa del Rey (Rekor Terbanyak)",
            description = "Klub kebanggaan Catalunya dengan semboyan 'Més que un club'. Terkenal di seluruh dunia berkat akademi muda La Masia dan filosofi menyerang yang mempesona.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 50,
            name = "Manchester City FC",
            code = "MCI",
            logo = "https://media.api-sports.io/football/teams/50.png",
            country = "England",
            foundedYear = 1880,
            president = "Khaldoon Al Mubarak",
            manager = "Pep Guardiola",
            stadiumName = "Etihad Stadium",
            stadiumCity = "Manchester",
            stadiumCapacity = 53400,
            stadiumImage = "https://media.api-sports.io/football/venues/555.png",
            website = "https://www.mancity.com",
            trophiesSummary = "1x Liga Champions Eropa, 10x Premier League, 7x FA Cup, 1x Piala Dunia Antarklub FIFA",
            description = "Kekuatan dominan di Inggris dan pemenang Treble Kontinental bersejarah tahun 2023 di bawah arsitektur taktik Pep Guardiola.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 42,
            name = "Arsenal FC",
            code = "ARS",
            logo = "https://media.api-sports.io/football/teams/42.png",
            country = "England",
            foundedYear = 1886,
            president = "Stan Kroenke & Josh Kroenke (KSE)",
            manager = "Mikel Arteta",
            stadiumName = "Emirates Stadium",
            stadiumCity = "London",
            stadiumCapacity = 60704,
            stadiumImage = "https://media.api-sports.io/football/venues/494.png",
            website = "https://www.arsenal.com",
            trophiesSummary = "13x Liga Inggris, 14x FA Cup (Rekor Terbanyak Inggris), 2x League Cup",
            description = "Klub bersejarah London Utara berjuluk The Gunners. Menorehkan rekor emas 'The Invincibles' di musim 2003/04 tanpa satu pun kekalahan.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 40,
            name = "Liverpool FC",
            code = "LIV",
            logo = "https://media.api-sports.io/football/teams/40.png",
            country = "England",
            foundedYear = 1892,
            president = "Tom Werner (Chairman) & John W. Henry",
            manager = "Arne Slot",
            stadiumName = "Anfield",
            stadiumCity = "Liverpool",
            stadiumCapacity = 61276,
            stadiumImage = "https://media.api-sports.io/football/venues/550.png",
            website = "https://www.liverpoolfc.com",
            trophiesSummary = "6x Liga Champions Eropa, 19x Premier League, 8x FA Cup, 10x EFL Cup",
            description = "Salah satu raksasa sepak bola paling dihormati di dunia, berakar di Merseyside dengan tribun legendaris The Kop dan semboyan 'You\\'ll Never Walk Alone'.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 33,
            name = "Manchester United FC",
            code = "MUN",
            logo = "https://media.api-sports.io/football/teams/33.png",
            country = "England",
            foundedYear = 1878,
            president = "Sir Jim Ratcliffe (INEOS) & Avram Glazer",
            manager = "Rúben Amorim",
            stadiumName = "Old Trafford (Theatre of Dreams)",
            stadiumCity = "Manchester",
            stadiumCapacity = 74310,
            stadiumImage = "https://media.api-sports.io/football/venues/556.png",
            website = "https://www.manutd.com",
            trophiesSummary = "3x Liga Champions Eropa, 20x Premier League (Rekor Inggris), 13x FA Cup",
            description = "The Red Devils, klub dengan basis suporter raksasa di seluruh pelosok dunia dan pemegang rekor 20 kali juara kasta tertinggi Inggris.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 157,
            name = "FC Bayern München",
            code = "BAY",
            logo = "https://media.api-sports.io/football/teams/157.png",
            country = "Germany",
            foundedYear = 1900,
            president = "Herbert Hainer",
            manager = "Vincent Kompany",
            stadiumName = "Allianz Arena",
            stadiumCity = "München",
            stadiumCapacity = 75000,
            stadiumImage = "https://media.api-sports.io/football/venues/700.png",
            website = "https://fcbayern.com",
            trophiesSummary = "6x Liga Champions Eropa, 33x Bundesliga Jerman, 20x DFB-Pokal",
            description = "Penguasa sejati sepak bola Jerman dengan semboyan 'Mia San Mia'. Dikenal memiliki manajemen profesional solid dan mentalitas juara tanpa kompromi.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 85,
            name = "Paris Saint-Germain",
            code = "PSG",
            logo = "https://media.api-sports.io/football/teams/85.png",
            country = "France",
            foundedYear = 1970,
            president = "Nasser Al-Khelaifi (QSI)",
            manager = "Luis Enrique",
            stadiumName = "Parc des Princes",
            stadiumCity = "Paris",
            stadiumCapacity = 47929,
            stadiumImage = "https://media.api-sports.io/football/venues/674.png",
            website = "https://en.psg.fr",
            trophiesSummary = "12x Ligue 1 Prancis, 15x Coupe de France, 9x Coupe de la Ligue",
            description = "Klub paling glamor di ibu kota Prancis yang kini bertransformasi menjadi kekuatan elit sepak bola Eropa modern.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 505,
            name = "Inter Milan",
            code = "INT",
            logo = "https://media.api-sports.io/football/teams/505.png",
            country = "Italy",
            foundedYear = 1908,
            president = "Giuseppe Marotta",
            manager = "Simone Inzaghi",
            stadiumName = "Stadio Giuseppe Meazza",
            stadiumCity = "Milano",
            stadiumCapacity = 75817,
            stadiumImage = "https://media.api-sports.io/football/venues/907.png",
            website = "https://www.inter.it",
            trophiesSummary = "3x Liga Champions Eropa, 20x Serie A Italia (Bintang Dua), 9x Coppa Italia",
            description = "Nerazzurri, satu-satunya klub Italia yang berhasil meraih Treble Winner bersejarah dan belum pernah terdegradasi dari kasta utama.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 496,
            name = "Juventus FC",
            code = "JUV",
            logo = "https://media.api-sports.io/football/teams/496.png",
            country = "Italy",
            foundedYear = 1897,
            president = "Gianluca Ferrero",
            manager = "Thiago Motta",
            stadiumName = "Allianz Stadium",
            stadiumCity = "Torino",
            stadiumCapacity = 41507,
            stadiumImage = "https://media.api-sports.io/football/venues/910.png",
            website = "https://www.juventus.com",
            trophiesSummary = "2x Liga Champions Eropa, 36x Serie A (Rekor Italia), 15x Coppa Italia",
            description = "La Vecchia Signora (Si Nyonya Tua) asal Turin, pemilik gelar Scudetto terbanyak di Italia dengan sejarah panjang tradisi bertahan dan kemenangan.",
            sportType = SportType.FOOTBALL
        ),
        ClubBio(
            id = 1001,
            name = "Los Angeles Lakers",
            code = "LAL",
            logo = "https://a.espncdn.com/i/teamlogos/nba/500/lal.png",
            country = "USA",
            foundedYear = 1947,
            president = "Jeanie Buss (Controlling Owner & President)",
            manager = "JJ Redick",
            stadiumName = "Crypto.com Arena",
            stadiumCity = "Los Angeles, California",
            stadiumCapacity = 19068,
            stadiumImage = "https://media.api-sports.io/basketball/venues/1.png",
            website = "https://www.nba.com/lakers",
            trophiesSummary = "17x Juara NBA (Rekor NBA bersama Celtics), 1x NBA In-Season Tournament",
            description = "Franchise paling ikonik dalam sejarah NBA. Rumah bagi para legenda basket dunia dari era Showtime hingga era LeBron James.",
            sportType = SportType.BASKETBALL
        ),
        ClubBio(
            id = 1002,
            name = "Golden State Warriors",
            code = "GSW",
            logo = "https://a.espncdn.com/i/teamlogos/nba/500/gsw.png",
            country = "USA",
            foundedYear = 1946,
            president = "Joe Lacob (CEO & Co-Executive Chairman)",
            manager = "Steve Kerr",
            stadiumName = "Chase Center",
            stadiumCity = "San Francisco, California",
            stadiumCapacity = 18064,
            stadiumImage = "https://media.api-sports.io/basketball/venues/2.png",
            website = "https://www.nba.com/warriors",
            trophiesSummary = "7x Juara NBA, 12x Juara Wilayah Barat",
            description = "Dinasti bola basket modern di San Francisco yang merevolusi gaya permainan basket dunia melalui kehebatan tembakan jarak jauh Stephen Curry.",
            sportType = SportType.BASKETBALL
        )
    )

    suspend fun getClubs(
        sportType: SportType = SportType.FOOTBALL,
        query: String = ""
    ): Result<List<ClubBio>> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()

        if (cleanQuery.isEmpty()) {
            val filtered = topWorldClubs.filter { it.sportType == sportType }
            return@withContext Result.success(filtered.ifEmpty { topWorldClubs })
        }

        val localMatches = topWorldClubs.filter {
            it.name.contains(cleanQuery, ignoreCase = true) ||
            (it.code?.contains(cleanQuery, ignoreCase = true) == true) ||
            (it.president?.contains(cleanQuery, ignoreCase = true) == true) ||
            (it.manager?.contains(cleanQuery, ignoreCase = true) == true) ||
            (it.stadiumName?.contains(cleanQuery, ignoreCase = true) == true)
        }

        // Jika sepak bola, coba cari juga dari API-Sports
        val onlineList = mutableListOf<ClubBio>()
        if (sportType.isFootball) {
            try {
                val resp = ApiClientFactory.getFootballApi().getTeams(search = cleanQuery)
                resp.response.forEach { item ->
                    val t = item.team ?: return@forEach
                    val v = item.venue
                    // Enrich jika ada data presiden di database lokal
                    val knownLocal = topWorldClubs.find { it.id == t.id || it.name.contains(t.name ?: "", ignoreCase = true) }
                    onlineList.add(
                        ClubBio(
                            id = t.id ?: 0,
                            name = t.name ?: "Unknown Club",
                            code = t.code,
                            logo = t.logo,
                            country = t.country,
                            foundedYear = t.founded,
                            president = knownLocal?.president ?: "Dewan Direksi Klub",
                            manager = knownLocal?.manager ?: "Pelatih Kepala",
                            stadiumName = v?.name ?: knownLocal?.stadiumName,
                            stadiumCity = v?.city ?: knownLocal?.stadiumCity,
                            stadiumCapacity = v?.capacity ?: knownLocal?.stadiumCapacity,
                            stadiumImage = v?.image ?: knownLocal?.stadiumImage,
                            website = knownLocal?.website,
                            trophiesSummary = knownLocal?.trophiesSummary,
                            description = knownLocal?.description ?: "Klub sepak bola profesional terdaftar resmi di liga ${t.country ?: "internasional"}.",
                            sportType = SportType.FOOTBALL
                        )
                    )
                }
            } catch (_: Exception) {
                // Gunakan pencarian lokal jika offline atau limit
            }
        }

        val combined = (onlineList + localMatches).distinctBy { it.id }
        Result.success(combined.ifEmpty { localMatches })
    }

    suspend fun getClubById(id: Int): ClubBio? = withContext(Dispatchers.IO) {
        val local = topWorldClubs.find { it.id == id }
        if (local != null) return@withContext local

        // Coba cari dari API-Sports
        try {
            val resp = ApiClientFactory.getFootballApi().getTeams(id = id)
            val item = resp.response.firstOrNull() ?: return@withContext null
            val t = item.team ?: return@withContext null
            val v = item.venue
            ClubBio(
                id = t.id ?: id,
                name = t.name ?: "Unknown Club",
                code = t.code,
                logo = t.logo,
                country = t.country,
                foundedYear = t.founded,
                president = "Presiden & Dewan Direksi",
                manager = "Pelatih Utama",
                stadiumName = v?.name,
                stadiumCity = v?.city,
                stadiumCapacity = v?.capacity,
                stadiumImage = v?.image,
                website = null,
                trophiesSummary = "Kompetisi Resmi Domestik & Internasional",
                description = "Profil lengkap klub resmi.",
                sportType = SportType.FOOTBALL
            )
        } catch (_: Exception) {
            null
        }
    }
}
