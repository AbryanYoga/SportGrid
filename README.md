# SportGrid ⚡

> **Universal Multi-Sport Live Dashboard & Intelligence**  
> Aplikasi Android modern berbasis **Jetpack Compose**, **Material 3**, dan **Hybrid Multi-Source Sports API Architecture** yang menghadirkan data skor langsung (*live scores*), jadwal pertandingan, klasemen liga, serta bursa transfer pemain di 5 cabang olahraga global.

---

## 🌐 Arsitektur Multi-Source API (Total: 3 External APIs + 1 Smart Cache Engine)

Aplikasi **SportGrid** tidak bergantung pada satu penyedia data tunggal. Dengan menerapkan **Multi-Source Data Aggregation & Failover Strategy**, aplikasi memanfaatkan keunggulan masing-masing API secara sinergis:

```
                      ┌─────────────────────────────────────────┐
                      │          UI Layer & ViewModel           │
                      └────────────────────┬────────────────────┘
                                           │
                                           ▼
                      ┌─────────────────────────────────────────┐
                      │        Repository Layer & Mapper        │
                      └───────┬────────────┬────────────┬───────┘
                              │            │            │
            ┌─────────────────┴─┐   ┌──────┴──────┐   ┌─┴────────────────┐
            ▼                   ▼   ▼             ▼   ▼                  ▼
     ┌──────────────┐     ┌───────────────┐     ┌────────────────┐ ┌───────────────┐
     │  1. ESPN API │     │ 2. API-Sports │     │3. Football-Data│ │4. Smart Cache │
     │  (Live Score)│     │  (Transfers)  │     │   (Fallback)   │ │  (In-Memory)  │
     └──────────────┘     └───────────────┘     └────────────────┘ └───────────────┘
```

### Rincian API yang Digunakan:

| No | Nama API | Endpoint & Protokol | Peran & Fokus Utama | Keunggulan Spesifik |
|:---:|:---|:---|:---|:---|
| **1** | **ESPN Public Scoreboard API** | `site.api.espn.com` (REST / JSON) | **Live Scores & Real-Time Clock**<br>Skor pertandingan langsung, status babak/kuarter, dan jadwal hari ini. | **100% Bebas Kuota (Unlimited)**, **Zero-Key (Keyless)**, update real-time detik-per-detik tanpa membebani limit kuota harian. |
| **2** | **API-Sports (Football & Games)** | `api-sports.io` (v3 Football & v1 Games) | **Bursa Transfer & Foto Pemain**<br>Riwayat perpindahan pemain, foto resmi atlet (*headshots CDN*), dan klasemen liga. | Data transfer terlengkap di dunia, logo klub HD, dan coverage 5 cabang olahraga global. |
| **3** | **Football-Data.org API** | `api.football-data.org` (v4 REST) | **Kompetisi Eropa & Jadwal Cadangan**<br>Data pertandingan alternatif liga-liga sepak bola Eropa. | Standar open-source paling populer di GitHub, struktur data terstandarisasi, failover terpercaya. |
| **4** | **Smart Local Cache Engine** | Internal In-Memory (`MemoryCache.kt`) | **Zero-Latency Navigation & Offline Fallback**<br>Manajemen TTL (60 detik – 24 jam) dan data demo cerdas dinamis. | Menjamin aplikasi tidak pernah mengalami layar kosong (*blank screen*) dan navigasi antar-tab instan tanpa lag. |

---

## 📱 Ringkasan Proyek

- **Nama Aplikasi:** SportGrid
- **Package Name:** `com.abryan.sportgrid`
- **Target OS:** Android 8.0 (API 26) hingga Android 16 (API 37)
- **Bahasa & Framework:** Kotlin 2.0, Jetpack Compose, Material 3
- **Komponen Inti:** Coroutines & Flow, Retrofit 2, OkHttp 3, Kotlinx Serialization, Coil 3, DataStore Preferences, Navigation Compose
- **Desain & Identitas:** *Electric Volt* (`#CCFF00`) di atas *Carbon Dark* (`#0C0F12`), Velocity Grid Monogram "S", Floating Dock Navigation Bar

---

## ✨ Fitur Utama

1. **Dashboard Olahraga Universal (Beranda)**
   - *Athlete Spotlight Stories*: Avatar sorotan atlet top dengan foto resmi dan cincin aksen neon volt.
   - *Hero Live Match Card*: Kartu pertandingan utama dengan status babak/kuartal dan skor langsung yang berkedip (*pulsing live indicator*).
   - *Sport Filter Chips*: Pilihan cepat 5 cabang olahraga (Sepak Bola, Bola Basket, Bola Voli, Bisbol, Hoki Es).
   - *Top Events & Favorite Cards*: Menampilkan statistik pertandingan, logo tim, dan pintasan *Key plays in this game*.
   - *Transfer Preview*: Ringkasan pergerakan bursa transfer terkini dengan foto pemain.

2. **Skor Langsung & Kalender Jadwal (Live Scores)**
   - Strip pemilih tanggal 7 hari (*horizontal date strip*).
   - Pembaruan otomatis (*auto-refresh*) berkala setiap 60 detik tanpa khawatir kehabisan kuota (ditenagai ESPN API).
   - Filter instan antar cabang olahraga.
   - Failover cerdas ke data jadwal hari ini saat jam jeda pertandingan.

3. **Eksplorasi Liga & Klasemen (Leagues & Standings)**
   - Daftar liga-liga top dunia (Premier League, La Liga, Serie A, NBA, MLB, dll.) dalam tampilan *Adaptive Grid*.
   - Bilah pencarian liga instan (*live search query*).
   - Halaman detail liga komprehensif: Tab **Jadwal & Skor** (*Fixtures*) dan Tab **Klasemen** (*Standings*) lengkap dengan poin, selisih gol, dan rekor menang/seri/kalah.

4. **Bursa Transfer Pemain (Transfer Market)**
   - Riwayat perpindahan pemain bintang dunia (foto pemain, klub asal, klub tujuan, tanggal transfer, dan biaya transfer).
   - Filter klub-klub raksasa Eropa (Real Madrid, Arsenal, Manchester City, Barcelona, Bayern Munich, PSG, Juventus, Liverpool).
   - Pengurutan otomatis dari transfer paling mutakhir (*descending order*).
   - Dukungan pencarian klub kustom.

5. **Profil Bintang, Pelatih Kepala & Bio Data Klub (Universal Profiles & Club Bio)**
   - **Pencarian Universal Pemain, Pelatih & Klub:** Mendukung pencarian online via API-Sports serta katalog bintang dunia terkurasi di 5 cabang olahraga (Sepak Bola, Bola Basket, Bola Voli, Bisbol, Hoki Es).
   - **Halaman Khusus Biodata Pemain (`PlayerDetailScreen`):** Berpindah ke layar penuh khusus saat pemain ditekan (menggantikan pop-up modal biasa), menampilkan potret resmi HD, nomor punggung, posisi, klub saat ini, usia, tinggi, berat badan, dominansi kaki/tangan, nilai pasar, gaya bermain, serta linimasa riwayat transfer karier klub dari masa ke masa dan lemari trofi penghargaan.
   - **Halaman Khusus Pelatih (`CoachDetailScreen`):** Layar penuh khusus menampilkan potret resmi, filosofi & gaya melatih, rekor gelar juara/trofi, serta linimasa karier manajerial klub dari tahun ke tahun.
   - **Bio Data Resmi Klub & Presiden Klub (`ClubDetailScreen`):** Profil mendalam klub dunia menampilkan **Presiden/Pemilik Klub** (Florentino Pérez, Joan Laporta, Stan Kroenke, Khaldoon Al Mubarak, Herbert Hainer, Nasser Al-Khelaifi, dll.), Pelatih Kepala, Foto & Kapasitas Stadion Utama, Lemari Trofi, Sejarah Klub, dan tautan situs resmi.

6. **Analisis Taktik, Formasi & Statistik Laga (`MatchDetailScreen`)**
   - **Taktik & Formasi Tim:** Menampilkan formasi kedua tim (misal: 4-3-3 vs 4-2-3-1), nama pelatih kepala, filosofi pendekatan permainan, visual lapangan taktik (*Pitch Formation Board*), dan daftar starting 11 pemain lengkap beserta nomor punggung.
   - **Statistik Komparasi Mendalam:** Grafik batang komparasi interaktif untuk penguasaan bola (*Possession %*), total tembakan (*Total Shots*), tembakan tepat sasaran (*Shots on Target*), akurasi operan (*Passing Accuracy*), pelanggaran (*Fouls*), tendangan sudut (*Corner Kicks*), kartu kuning & merah, *Offsides*, dan penyelamatan kiper (*Saves*).
   - **Linimasa Pertandingan:** Kronologi menit demi menit peristiwa gol, kartu wasit, dan pergantian pemain.

7. **Optimasi Mode Landscape & Animasi Fluid GSAP**
   - **Tampilan Landscape Responsif (Dual-Column Dashboard):** Di mode horizontal, tata letak otomatis membagi layar menjadi 2 kolom (Kolom Kiri: *Athlete Spotlight*, *Hero Live Match*, dan *Banner*; Kolom Kanan: *Quick Explore*, *Sport Chips*, *Today's Fixtures*, dan *Transfers*) dengan *floating bottom bar* yang ramping sehingga layar tidak terasa sempit.
   - **Animasi Fisika Jetpack Compose (GSAP-Style):** Menggunakan *physics-based spring motion* untuk transisi sentuhan (*bouncy press*), *continuous living radar glow* pada kartu pertandingan langsung, serta efek *staggered spring entrance*.

8. **Pengaturan Tema & Multi-Bahasa (Settings)**
   - **Mode Tema:** Mendukung *Carbon Dark* (Gelap), *Light* (Terang), dan *Ikuti Sistem*.
   - **Bahasa Aplikasi (Per-App Language):** Bahasa Indonesia dan English (US) dengan pergantian dinamis tanpa perlu restart ponsel.
   - Konfigurasi status API dan informasi cache.

---

## 🏗️ Struktur Arsitektur Direktori

```
app/src/main/java/com/abryan/sportgrid/
├── data/
│   ├── local/          # MemoryCache (TTL), SettingsDataStore, DemoFallbackData
│   ├── mapper/         # SportMapper (Konversi DTO ke Domain Model seragam)
│   ├── remote/
│   │   ├── api/        # Retrofit Interfaces: EspnApiService, FootballDataApiService,
│   │   │               # FootballApiService, GamesApiService, ApiClientFactory
│   │   └── dto/        # DTO: EspnDto, FootballDataDto, FootballDto, GamesDto
│   └── repository/     # MatchRepository (Multi-Source), LeagueRepository, TransferRepository
├── di/                 # SportGridContainer (Manual Dependency Injection)
├── domain/
│   └── model/          # Domain Models: Match, League, Standing, Transfer, SportType
├── ui/
│   ├── components/     # MatchCard, HeroLiveCard, LeagueCard, TransferCard, BottomNavBar
│   ├── navigation/     # NavDestination & SportGridNavHost
│   ├── screens/        # Screen & ViewModel (Home, Live, Leagues, Transfers, Settings)
│   └── theme/          # Color, Theme, Typography, Spacing
└── SportGridApp.kt     # Application Initialization & DI Setup
```

---

## 🚀 Panduan Setup & Konfigurasi API

Aplikasi telah siap dijalankan langsung (*out-of-the-box*) berkat ketersediaan **ESPN Public API** dan **Football-Data.org API**.

Untuk mengaktifkan fitur data mendalam dari **API-Sports**:
1. Dapatkan API Key gratis di [dashboard.api-sports.io](https://dashboard.api-sports.io/).
2. Buka file `local.properties` di root folder proyek:
   ```properties
   API_SPORTS_KEY=masukkan_api_key_anda_disini
   ```
3. Build dan jalankan aplikasi:
   ```powershell
   .\gradlew.bat assembleDebug
   ```

---

## 🎨 Palet Warna & Identitas Desain

| Token Warna | Nilai Hex | Fungsi / Penggunaan |
|:---|:---|:---|
| **Electric Volt** | `#CCFF00` | Aksen primer, tombol CTA, live badge, border sorotan aktif |
| **Volt Neon Glow** | `#E5FF66` | Efek hover, gradien hero card, aksen sekunder |
| **Carbon Dark** | `#0C0F12` | Background utama mode gelap (Deep OLED Charcoal) |
| **Surface Dark** | `#161B22` | Background kartu, floating dock bar, search input |
| **Volt Green Live** | `#00E676` | Indikator pertandingan yang sedang berlangsung (*LIVE dot*) |

---

## 📄 Lisensi & Standar Rekayasa Perangkat Lunak

Proyek ini dibangun mengikuti prinsip **Clean Architecture**, **MVVM**, **SOLID Principles**, serta standar modern **Android Jetpack**. Dilindungi di bawah lisensi terbuka untuk keperluan edukasi dan evaluasi teknis.
