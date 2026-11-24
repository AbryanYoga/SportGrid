package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.remote.api.ApiClientFactory
import com.abryan.sportgrid.domain.model.CareerItem
import com.abryan.sportgrid.domain.model.PersonProfile
import com.abryan.sportgrid.domain.model.PersonRole
import com.abryan.sportgrid.domain.model.SportType
import com.abryan.sportgrid.util.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PersonRepository {

    private val profileCache = MemoryCache<List<PersonProfile>>(AppConfig.TTL_LEAGUES_MS)

    // Bintang dan pelatih top dunia untuk 5 cabang olahraga
    val featuredWorldwideProfiles: List<PersonProfile> = listOf(
        // Sepak Bola - Pemain
        PersonProfile(
            id = "f_p1",
            name = "Kylian Mbappé",
            role = PersonRole.PLAYER,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/players/278.png",
            age = 27,
            nationality = "France",
            teamName = "Real Madrid",
            teamLogo = "https://media.api-sports.io/football/teams/541.png",
            number = 9,
            position = "Penyerang (Forward)",
            height = "178 cm",
            weight = "75 kg",
            careerSummary = "Pemenang Piala Dunia 2018, top scorer Ligue 1 6x, bintang timnas Prancis & Real Madrid."
        ),
        PersonProfile(
            id = "f_p2",
            name = "Jude Bellingham",
            role = PersonRole.PLAYER,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/players/152982.png",
            age = 22,
            nationality = "England",
            teamName = "Real Madrid",
            teamLogo = "https://media.api-sports.io/football/teams/541.png",
            number = 5,
            position = "Gelandang (Midfielder)",
            height = "186 cm",
            weight = "75 kg",
            careerSummary = "Juara Liga Champions 2024, Golden Boy 2023, motor serangan Real Madrid."
        ),
        PersonProfile(
            id = "f_p3",
            name = "Erling Haaland",
            role = PersonRole.PLAYER,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/players/1100.png",
            age = 25,
            nationality = "Norway",
            teamName = "Manchester City",
            teamLogo = "https://media.api-sports.io/football/teams/50.png",
            number = 9,
            position = "Penyerang (Forward)",
            height = "194 cm",
            weight = "88 kg",
            careerSummary = "Treble Winner 2023, rekor pencetak gol terbanyak dalam satu musim Premier League."
        ),
        PersonProfile(
            id = "f_p4",
            name = "Lionel Messi",
            role = PersonRole.PLAYER,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/players/154.png",
            age = 38,
            nationality = "Argentina",
            teamName = "Inter Miami",
            teamLogo = "https://media.api-sports.io/football/teams/1603.png",
            number = 10,
            position = "Penyerang (Playmaker)",
            height = "170 cm",
            weight = "72 kg",
            careerSummary = "8x Ballon d'Or, Juara Piala Dunia 2022, legenda sepak bola sepanjang masa."
        ),
        PersonProfile(
            id = "f_p5",
            name = "Cristiano Ronaldo",
            role = PersonRole.PLAYER,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/players/874.png",
            age = 41,
            nationality = "Portugal",
            teamName = "Al Nassr",
            teamLogo = "https://media.api-sports.io/football/teams/2939.png",
            number = 7,
            position = "Penyerang (Forward)",
            height = "187 cm",
            weight = "83 kg",
            careerSummary = "5x Ballon d'Or, 5x Juara Liga Champions, top scorer sepanjang masa dunia sepak bola."
        ),
        PersonProfile(
            id = "f_p6",
            name = "Lamine Yamal",
            role = PersonRole.PLAYER,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/players/361099.png",
            age = 18,
            nationality = "Spain",
            teamName = "Barcelona",
            teamLogo = "https://media.api-sports.io/football/teams/529.png",
            number = 19,
            position = "Sayap (Winger)",
            height = "180 cm",
            weight = "68 kg",
            careerSummary = "Juara Euro 2024 bersama Spanyol, pemain muda terbaik dunia, fenomena La Masia."
        ),

        // Sepak Bola - Pelatih
        PersonProfile(
            id = "f_c1",
            name = "Carlo Ancelotti",
            role = PersonRole.COACH,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/coachs/2407.png",
            age = 66,
            nationality = "Italy",
            teamName = "Real Madrid",
            teamLogo = "https://media.api-sports.io/football/teams/541.png",
            position = "Pelatih Kepala (Manager)",
            coachingStyle = "Man-Management Adaptif, Kebebasan Berkreasi Pemain & Transisi Cepat",
            careerSummary = "Satu-satunya pelatih peraih 5 trofi Liga Champions Eropa dan juara di 5 liga top Eropa (La Liga, Premier League, Serie A, Bundesliga, Ligue 1).",
            achievements = listOf(
                "5x Juara Liga Champions UEFA (2003, 2007, 2014, 2022, 2024)",
                "Juara di 5 Liga Top Eropa (Spanyol, Inggris, Italia, Jerman, Prancis)",
                "3x Piala Super UEFA & 3x Piala Dunia Antarklub FIFA"
            ),
            careerHistory = listOf(
                CareerItem(teamName = "Real Madrid", startYear = "2021", endYear = "Sekarang", roleTitle = "Manager"),
                CareerItem(teamName = "Everton", startYear = "2019", endYear = "2021", roleTitle = "Manager"),
                CareerItem(teamName = "Napoli", startYear = "2018", endYear = "2019", roleTitle = "Head Coach"),
                CareerItem(teamName = "Bayern München", startYear = "2016", endYear = "2017", roleTitle = "Head Coach"),
                CareerItem(teamName = "Real Madrid", startYear = "2013", endYear = "2015", roleTitle = "Manager (La Décima)"),
                CareerItem(teamName = "Paris Saint-Germain", startYear = "2011", endYear = "2013", roleTitle = "Manager"),
                CareerItem(teamName = "Chelsea", startYear = "2009", endYear = "2011", roleTitle = "Manager"),
                CareerItem(teamName = "AC Milan", startYear = "2001", endYear = "2009", roleTitle = "Head Coach")
            )
        ),
        PersonProfile(
            id = "f_c2",
            name = "Pep Guardiola",
            role = PersonRole.COACH,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/coachs/4.png",
            age = 55,
            nationality = "Spain",
            teamName = "Manchester City",
            teamLogo = "https://media.api-sports.io/football/teams/50.png",
            position = "Pelatih Kepala (Manager)",
            coachingStyle = "Positional Play (Juego de Posición), High Pressing & Inverted Full-backs",
            careerSummary = "Pionir taktik modern, peraih 2x Treble Winners kontinental (Barcelona 2009 & Manchester City 2023), 6x juara Premier League.",
            achievements = listOf(
                "3x Juara Liga Champions UEFA (2009, 2011, 2023)",
                "6x Juara Premier League Inggris (4 kali beruntun)",
                "2x Treble Winner (Barcelona 2009 & Man City 2023)",
                "4x Juara Piala Super UEFA & 4x Piala Dunia Antarklub"
            ),
            careerHistory = listOf(
                CareerItem(teamName = "Manchester City", startYear = "2016", endYear = "Sekarang", roleTitle = "Manager"),
                CareerItem(teamName = "Bayern München", startYear = "2013", endYear = "2016", roleTitle = "Head Coach"),
                CareerItem(teamName = "FC Barcelona", startYear = "2008", endYear = "2012", roleTitle = "Head Coach")
            )
        ),
        PersonProfile(
            id = "f_c3",
            name = "Mikel Arteta",
            role = PersonRole.COACH,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/coachs/19.png",
            age = 44,
            nationality = "Spain",
            teamName = "Arsenal",
            teamLogo = "https://media.api-sports.io/football/teams/42.png",
            position = "Pelatih Kepala (Manager)",
            coachingStyle = "Struktur Pertahanan Kokoh, Set-Piece Dominance & Fluid Attacking",
            careerSummary = "Membawa revolusi Arsenal kembali menjadi tim penantang gelar juara Premier League dan kembali ke panggung Liga Champions.",
            achievements = listOf(
                "1x Juara FA Cup (2020)",
                "2x Juara FA Community Shield (2020, 2023)",
                "Premier League Manager of the Month berulang kali"
            ),
            careerHistory = listOf(
                CareerItem(teamName = "Arsenal", startYear = "2019", endYear = "Sekarang", roleTitle = "Manager"),
                CareerItem(teamName = "Manchester City", startYear = "2016", endYear = "2019", roleTitle = "Asisten Pelatih")
            )
        ),
        PersonProfile(
            id = "f_c4",
            name = "Hansi Flick",
            role = PersonRole.COACH,
            sportType = SportType.FOOTBALL,
            photo = "https://media.api-sports.io/football/coachs/11.png",
            age = 61,
            nationality = "Germany",
            teamName = "FC Barcelona",
            teamLogo = "https://media.api-sports.io/football/teams/529.png",
            position = "Pelatih Kepala (Manager)",
            coachingStyle = "Ultra High Defensive Line, Aggressive Gegenpressing & Vertikal Cepat",
            careerSummary = "Pelatih peraih Sextuple bersejarah bersama Bayern Munich pada tahun 2020, kini memimpin kebangkitan Barcelona.",
            achievements = listOf(
                "Sextuple Winner (6 Trofi dalam satu musim bersama Bayern 2020)",
                "Juara Liga Champions UEFA (2020)",
                "2x Bundesliga & 1x DFB-Pokal"
            ),
            careerHistory = listOf(
                CareerItem(teamName = "FC Barcelona", startYear = "2024", endYear = "Sekarang", roleTitle = "Head Coach"),
                CareerItem(teamName = "Timnas Jerman", startYear = "2021", endYear = "2023", roleTitle = "Head Coach"),
                CareerItem(teamName = "Bayern München", startYear = "2019", endYear = "2021", roleTitle = "Head Coach (Sextuple)")
            )
        ),

        // Bola Basket - Pemain & Pelatih
        PersonProfile(
            id = "b_p1",
            name = "Stephen Curry",
            role = PersonRole.PLAYER,
            sportType = SportType.BASKETBALL,
            photo = "https://a.espncdn.com/i/headshots/nba/players/full/3975.png",
            age = 37,
            nationality = "USA",
            teamName = "Golden State Warriors",
            number = 30,
            position = "Point Guard",
            height = "188 cm",
            weight = "84 kg",
            careerSummary = "4x Juara NBA, 2x MVP, pencetak tembakan 3 poin terbanyak dalam sejarah bola basket."
        ),
        PersonProfile(
            id = "b_p2",
            name = "LeBron James",
            role = PersonRole.PLAYER,
            sportType = SportType.BASKETBALL,
            photo = "https://a.espncdn.com/i/headshots/nba/players/full/1966.png",
            age = 41,
            nationality = "USA",
            teamName = "Los Angeles Lakers",
            number = 23,
            position = "Forward",
            height = "206 cm",
            weight = "113 kg",
            careerSummary = "4x Juara NBA, 4x Finals MVP, pencetak poin terbanyak sepanjang sejarah NBA."
        ),
        PersonProfile(
            id = "b_c1",
            name = "Steve Kerr",
            role = PersonRole.COACH,
            sportType = SportType.BASKETBALL,
            photo = "https://a.espncdn.com/combiner/i?img=/i/headshots/nba/coaches/full/683.png",
            age = 60,
            nationality = "USA",
            teamName = "Golden State Warriors",
            position = "Head Coach",
            coachingStyle = "Pace and Space, Ball Movement & Motion Offense",
            careerSummary = "Pelatih dinasti modern Warriors (4 cincin juara NBA) serta Head Coach Timnas Amerika Serikat peraih medali emas Olimpiade Paris 2024.",
            achievements = listOf(
                "4x Juara NBA sebagai Head Coach (2015, 2017, 2018, 2022)",
                "Medali Emas Olimpiade Paris 2024 (Team USA)",
                "NBA Coach of the Year (2016)",
                "Rekor 73 Kemenangan Reguler NBA (2015-16)"
            ),
            careerHistory = listOf(
                CareerItem(teamName = "Golden State Warriors", startYear = "2014", endYear = "Sekarang", roleTitle = "Head Coach"),
                CareerItem(teamName = "USA Basketball Men's Team", startYear = "2021", endYear = "2024", roleTitle = "Head Coach"),
                CareerItem(teamName = "Phoenix Suns", startYear = "2007", endYear = "2010", roleTitle = "General Manager")
            )
        ),

        // Bola Voli
        PersonProfile(
            id = "v_p1",
            name = "Yuji Nishida",
            role = PersonRole.PLAYER,
            sportType = SportType.VOLLEYBALL,
            photo = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d7/Yuji_Nishida_2021.jpg/440px-Yuji_Nishida_2021.jpg",
            age = 26,
            nationality = "Japan",
            teamName = "Japan National Team",
            number = 11,
            position = "Opposite Spiker",
            height = "186 cm",
            weight = "87 kg",
            careerSummary = "Bintang voli dunia asal Jepang dengan lompatan vertikal legendaris dan jump serve mematikan."
        ),
        PersonProfile(
            id = "v_p2",
            name = "Ran Takahashi",
            role = PersonRole.PLAYER,
            sportType = SportType.VOLLEYBALL,
            photo = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/Ran_Takahashi_2022.jpg/440px-Ran_Takahashi_2022.jpg",
            age = 24,
            nationality = "Japan",
            teamName = "Suntory Sunbirds",
            number = 12,
            position = "Outside Hitter",
            height = "190 cm",
            weight = "83 kg",
            careerSummary = "Pilar timnas Jepang peraih medali VNL, memiliki kemampuan receive dan spike kelas dunia."
        ),

        // Bisbol
        PersonProfile(
            id = "bb_p1",
            name = "Shohei Ohtani",
            role = PersonRole.PLAYER,
            sportType = SportType.BASEBALL,
            photo = "https://a.espncdn.com/i/headshots/mlb/players/full/39832.png",
            age = 31,
            nationality = "Japan",
            teamName = "Los Angeles Dodgers",
            number = 17,
            position = "Designated Hitter / Pitcher",
            height = "193 cm",
            weight = "95 kg",
            careerSummary = "Fenomena bisbol global dua arah (Pitcher & Hitter), Juara World Series 2024, MVP MLB berulang kali."
        ),

        // Hoki Es
        PersonProfile(
            id = "h_p1",
            name = "Connor McDavid",
            role = PersonRole.PLAYER,
            sportType = SportType.HOCKEY,
            photo = "https://a.espncdn.com/i/headshots/nhl/players/full/3895074.png",
            age = 29,
            nationality = "Canada",
            teamName = "Edmonton Oilers",
            number = 97,
            position = "Center",
            height = "185 cm",
            weight = "88 kg",
            careerSummary = "Kapten Edmonton Oilers, peraih Conn Smythe Trophy & Hart Trophy, pemain hoki tercepat di dunia."
        )
    )

    suspend fun getProfiles(
        sportType: SportType,
        role: PersonRole = PersonRole.ALL,
        query: String = ""
    ): Result<List<PersonProfile>> = withContext(Dispatchers.IO) {
        val cleanQuery = query.trim()

        if (cleanQuery.isEmpty()) {
            val list = featuredWorldwideProfiles.filter {
                (it.sportType == sportType) &&
                (role == PersonRole.ALL || it.role == role)
            }
            return@withContext Result.success(list)
        }

        // Cari online dari API-Sports jika sepak bola atau bola basket
        val onlineResults = mutableListOf<PersonProfile>()
        try {
            if (sportType.isFootball) {
                if (role == PersonRole.ALL || role == PersonRole.PLAYER) {
                    val playerResp = ApiClientFactory.getFootballApi().searchPlayerProfiles(cleanQuery)
                    playerResp.response.forEach { item ->
                        val p = item.player ?: return@forEach
                        onlineResults.add(
                            PersonProfile(
                                id = "api_p_${p.id}",
                                name = p.name ?: "${p.firstname} ${p.lastname}".trim(),
                                role = PersonRole.PLAYER,
                                sportType = SportType.FOOTBALL,
                                photo = p.photo,
                                age = p.age,
                                nationality = p.nationality,
                                teamName = null,
                                teamLogo = null,
                                number = p.number,
                                position = p.position,
                                height = p.height?.let { "$it cm" },
                                weight = p.weight?.let { "$it kg" },
                                careerSummary = null
                            )
                        )
                    }
                }

                if (role == PersonRole.ALL || role == PersonRole.COACH) {
                    val coachResp = ApiClientFactory.getFootballApi().searchCoaches(cleanQuery)
                    coachResp.response.forEach { c ->
                        onlineResults.add(
                            PersonProfile(
                                id = "api_c_${c.id}",
                                name = c.name ?: "${c.firstname} ${c.lastname}".trim(),
                                role = PersonRole.COACH,
                                sportType = SportType.FOOTBALL,
                                photo = c.photo,
                                age = c.age,
                                nationality = c.nationality,
                                teamName = c.team?.name,
                                teamLogo = c.team?.logo,
                                position = "Pelatih Kepala (Manager)",
                                height = c.height,
                                weight = c.weight,
                                careerSummary = c.career?.firstOrNull()?.team?.name?.let { "Klub Terakhir: $it" }
                            )
                        )
                    }
                }
            } else if (sportType == SportType.BASKETBALL) {
                if (role == PersonRole.ALL || role == PersonRole.PLAYER) {
                    val basketResp = ApiClientFactory.getGamesApi(SportType.BASKETBALL).searchBasketballPlayers(cleanQuery)
                    basketResp.response.forEach { bp ->
                        onlineResults.add(
                            PersonProfile(
                                id = "api_bp_${bp.id}",
                                name = bp.name ?: "Unknown Player",
                                role = PersonRole.PLAYER,
                                sportType = SportType.BASKETBALL,
                                photo = null,
                                age = bp.age,
                                nationality = bp.country,
                                number = bp.number?.toIntOrNull(),
                                position = bp.position,
                                careerSummary = "Pemain Basket Profesional"
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Lanjut ke pencarian data lokal jika offline atau limit
        }

        // Cari juga di koleksi featured lokal
        val localMatches = featuredWorldwideProfiles.filter {
            it.sportType == sportType &&
            (role == PersonRole.ALL || it.role == role) &&
            it.name.contains(cleanQuery, ignoreCase = true)
        }

        val combined = (onlineResults + localMatches).distinctBy { it.name.lowercase() }
        Result.success(combined.ifEmpty { localMatches })
    }

    suspend fun getCoachById(id: String): PersonProfile? = withContext(Dispatchers.IO) {
        val local = featuredWorldwideProfiles.find { it.id == id && it.role == PersonRole.COACH }
        if (local != null) return@withContext local

        // Jika ID berasal dari API-Sports online (misal: "api_c_2407")
        if (id.startsWith("api_c_")) {
            val apiId = id.removePrefix("api_c_").toIntOrNull()
            if (apiId != null) {
                try {
                    // Cari via search coach API
                    val resp = ApiClientFactory.getFootballApi().searchCoaches(apiId.toString())
                    val coach = resp.response.firstOrNull()
                    if (coach != null) {
                        return@withContext PersonProfile(
                            id = id,
                            name = coach.name ?: "${coach.firstname} ${coach.lastname}".trim(),
                            role = PersonRole.COACH,
                            sportType = SportType.FOOTBALL,
                            photo = coach.photo,
                            age = coach.age,
                            nationality = coach.nationality,
                            teamName = coach.team?.name,
                            teamLogo = coach.team?.logo,
                            position = "Pelatih Kepala (Manager)",
                            height = coach.height,
                            weight = coach.weight,
                            careerSummary = coach.career?.firstOrNull()?.team?.name?.let { "Klub Saat Ini: $it" } ?: "Pelatih Sepak Bola Berlisensi Pro UEFA",
                            careerHistory = coach.career?.map {
                                CareerItem(
                                    teamName = it.team?.name ?: "Unknown Team",
                                    teamLogo = it.team?.logo,
                                    startYear = it.start,
                                    endYear = it.end ?: "Sekarang",
                                    roleTitle = "Pelatih"
                                )
                            } ?: emptyList()
                        )
                    }
                } catch (_: Exception) {}
            }
        }
        null
    }
}
