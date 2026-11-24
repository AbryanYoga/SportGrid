# SportGrid ⚡

> **Universal Multi-Sport Live Dashboard & Intelligence**  
> Aplikasi Android modern berbasis **Jetpack Compose**, **Material 3**, dan **API-Sports** yang menghadirkan data skor langsung (*live scores*), jadwal pertandingan, klasemen liga, serta bursa transfer pemain di 5 cabang olahraga global.

---

## 📱 Ringkasan Proyek

- **Nama Aplikasi:** SportGrid
- **Package Name:** `com.abryan.sportgrid`
- **Target OS:** Android 8.0 (API 26) hingga Android 16 (API 37)
- **Teknologi Utama:** Kotlin, Jetpack Compose, Material 3, Coroutines & Flow, Retrofit 2, Kotlinx Serialization, Coil 3, DataStore Preferences.
- **Desain & Identitas:** *Electric Volt* (`#CCFF00`) di atas *Carbon Dark* (`#0C0F12`), Velocity Grid Monogram "S", Floating Dock Navigation Bar.

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
   - Filter cepat antar cabang olahraga.
   - Mekanisme pembaruan otomatis (*auto-refresh*) berkala.
   - Mode data cadangan cerdas (*smart fallback*) apabila kuota API harian habis atau koneksi terputus.

3. **Eksplorasi Liga & Klasemen (Leagues & Standings)**
   - Daftar liga-liga top dunia (Premier League, La Liga, Serie A, NBA, MLB, dll.) dalam tampilan *Adaptive Grid*.
   - Bilah pencarian liga instan (*live search query*).
   - Halaman detail liga komprehensif: Tab **Jadwal & Skor** (*Fixtures*) dan Tab **Klasemen** (*Standings*) lengkap dengan poin, selisih gol, dan rekor menang/seri/kalah.

4. **Bursa Transfer Pemain (Transfer Market)**
   - Riwayat perpindahan pemain bintang dunia (foto pemain, klub asal, klub tujuan, tanggal transfer, dan tipe/biaya transfer).
   - Filter klub-klub raksasa Eropa (Real Madrid, Arsenal, Manchester City, Barcelona, Bayern Munich, PSG, Juventus, Liverpool).
   - Dukungan pencarian klub kustom.

5. **Pengaturan Tema & Multi-Bahasa (Settings)**
   - **Mode Tema:** Mendukung *Carbon Dark* (Gelap), *Light* (Terang), dan *Ikuti Sistem*.
   - **Bahasa Aplikasi (Per-App Language):** Bahasa Indonesia dan English (US) dengan pergantian dinamis tanpa perlu restart ponsel.
   - Konfigurasi status API dan informasi cache.

---

## 🏗️ Arsitektur & Pola Desain

Aplikasi dibangun mengikuti standar **Modern Android Architecture (MVVM + Repository Pattern)**:

```
app/src/main/java/com/abryan/sportgrid/
├── data/
│   ├── api/          # Retrofit Service (FootballApiService, GamesApiService, ApiClientFactory)
│   ├── dto/          # Data Transfer Objects (FootballDto, GamesDto)
│   ├── local/        # In-Memory Cache (MemoryCache) & Smart Fallback (DemoFallbackData)
│   ├── mapper/       # Mapper DTO ke Model Domain (SportMapper)
│   ├── model/        # Model Domain (Match, League, Team, Transfer, SportType)
│   ├── repository/   # Repository Layer (MatchRepository, LeagueRepository, TransferRepository)
│   └── settings/     # DataStore Preferences (SettingsDataStore)
├── di/               # Manual Dependency Injection Container (SportGridContainer)
├── ui/
│   ├── components/   # Reusable UI Cards (MatchCard, HeroLiveCard, LeagueCard, TransferCard, dll.)
│   ├── navigation/   # Navigation Compose Host & Floating Dock Bottom Bar
│   ├── screens/      # Screen & ViewModel (Home, Live, Leagues, Transfers, Settings)
│   └── theme/        # Material 3 Theme, Color Scheme (Electric Volt), Typography, Spacing
└── SportGridApp.kt   # Application Class (Inisialisasi Container & DataStore)
```

### Penanganan Komponen Kurikulum RPL (Rubrik Penilaian)
- **RecyclerView / List Komponen:** Menggunakan komponen modern `LazyColumn`, `LazyRow`, dan `LazyVerticalGrid` dengan *key-based items* dan *item card composables* reusable yang efisien dalam pemakaian memori.
- **Konsumsi API:** Menggunakan `Retrofit 2` dengan `kotlinx.serialization.json` dan interceptor header autentikasi (`x-apisports-key`).
- **Manajemen Cache:** Repository dilengkapi dengan `MemoryCache<T>` ber-TTL (*Time-To-Live*) 5–15 menit untuk menghemat kuota harian API-Sports (100 request/hari pada free tier).

---

## 🚀 Panduan Setup & Konfigurasi API Key

Aplikasi ini menggunakan layanan dari [API-Sports](https://api-sports.io/).

1. **Dapatkan API Key:**
   - Daftar akun gratis di [dashboard.api-sports.io](https://dashboard.api-sports.io/).
   - Salin **API Key** Anda dari dashboard.

2. **Pasang API Key di Proyek:**
   - Buka file `local.properties` di root folder proyek (file ini aman dan tidak akan ter-push ke repository Git).
   - Tambahkan baris berikut:
     ```properties
     API_SPORTS_KEY=masukkan_api_key_anda_disini
     ```

3. **Build & Jalankan Aplikasi:**
   - Buka project di Android Studio.
   - Sambungkan HP Android atau jalankan Emulator.
   - Klik tombol **Run 'app'** (`Shift + F10`) atau jalankan perintah:
     ```powershell
     .\gradlew.bat installDebug
     ```

> **Catatan:** Apabila API Key belum diatur atau kuota harian telah habis, aplikasi secara otomatis beralih ke **Smart Demo Fallback Data**, sehingga seluruh tampilan UI tetap terisi data representatif dan tidak akan mengalami layar kosong (*blank screen*).

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

## 👤 Pengembang Proyek

- **Nama:** Abryan Yoga
- **Institusi:** SMK Negeri 6 Surakarta
- **Kompetensi Keahlian:** Rekayasa Perangkat Lunak (RPL)
- **Mata Pelajaran:** Pemrograman Perangkat Bergerak (Mobile Application Development)
