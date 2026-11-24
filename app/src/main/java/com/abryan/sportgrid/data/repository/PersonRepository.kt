package com.abryan.sportgrid.data.repository

import com.abryan.sportgrid.data.remote.api.ApiClientFactory
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
            careerSummary = "Satu-satunya pelatih peraih 5 trofi Liga Champions Eropa dan juara di 5 liga top Eropa."
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
            careerSummary = "Pionir taktik tiki-taka, 2x Treble winner (Barcelona & Man City), 6x juara Premier League."
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
            careerSummary = "Membawa revolusi Arsenal kembali menjadi penantang gelar juara Premier League."
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
            careerSummary = "Pelatih dinasti Warriors (4 cincin juara NBA) dan pelatih Timnas USA di Olimpiade."
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
}
