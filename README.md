# 📱 Skor Siswa - SMA NEGERI 1 Keritang

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)
![Min SDK](https://img.shields.io/badge/Min_SDK-Android_7.0_(API_24)-blue)
![Language](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)
![UI](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?logo=jetpackcompose&logoColor=white)
![Database](https://img.shields.io/badge/Database-Room_SQLite-orange)

Aplikasi manajemen, pencatatan, dan penghitungan skor pelanggaran kedisiplinan siswa modern untuk **SMA NEGERI 1 Keritang**. Dirancang untuk memudahkan Tim Bimbingan Konseling (BK), Guru Piket, Siswa, serta Orang Tua/Wali Murid dalam memantau rekam jejak tata tertib sekolah secara transparan, akurat, dan real-time.

---

## 📥 Cara Download & Install APK

### Unduh Langsung:
1. Buka halaman **[Releases](../../releases)** di repositori GitHub ini.
2. Unduh file **`app-debug.apk`** atau **`Skor-Siswa-SMAN1Keritang.apk`**.
3. Buka file APK di HP Android Anda.
4. Jika muncul peringatan keamanan *"Izinkan instalasi dari sumber ini / Unknown sources"*, pilih **Izinkan (Allow)**.
5. Klik **Instal**, dan aplikasi siap digunakan!

---

## 🔑 Informasi Login & Akun Default

Aplikasi ini menggunakan sistem **Otorisasi Multi-Peran**:

| Peran Akun | Cara Akses | Keterangan |
| :--- | :--- | :--- |
| **Guru BK / Kesiswaan** | Masukkan Nama & PIN: `1234` *(Master: `bk123`)* | Akses penuh: Catat kasus, tambah/edit data siswa, ubah nama sekolah, reset data, kelola PIN. |
| **Guru Piket / Pengajar** | Masukkan Nama & PIN: `1234` | Akses pencatatan pelanggaran harian siswa saat jam piket/KBM. |
| **Siswa / Orang Tua** | Masukkan NIS Siswa (misal: `1001`) | Akses pantau mandiri: Lihat sisa poin, riwayat teguran, dan status pembinaan tanpa PIN. |

> 💡 *Nama Sekolah, Nama Koordinator BK, dan PIN Keamanan dapat diubah sewaktu-waktu melalui ikon **Pengaturan (Gerigi)** di pojok kanan atas setelah login sebagai Guru BK.*

---

## ✨ Fitur Utama

- 📋 **37 Aturan Pelanggaran Standar & 5 Kategori**
  - Keterlambatan, Kerapian/Seragam, Sikap & Etika, Rokok/Vape, hingga Pelanggaran Berat (Tawuran, Bolos, dll.).
- 📊 **Kalkulasi Skor Akumulasi Otomatis**
  - **Aman (Tertib)**: 0 Poin
  - **Pembinaan Ringan**: 1 - 24 Poin
  - **Surat Peringatan 1 (SP 1)**: 25 - 49 Poin
  - **Surat Peringatan 2 (SP 2)**: 50 - 74 Poin
  - **Surat Peringatan 3 (SP 3 / Panggilan Ortu)**: 75 - 100+ Poin
- 💬 **Kirim Notifikasi WhatsApp Resmi ke Wali Murid**
  - Template pesan resmi otomatis berisikan kop **SMA NEGERI 1 Keritang**, rincian tanggal, nama pelanggaran, skor terpotong, sisa poin, dan tindak lanjut BK.
- 📑 **Ekspor Rekapitulasi & Rekam Jejak**
  - Cetak atau salin laporan rekapitulasi per kelas atau per siswa secara instan untuk rapat evaluasi kesiswaan.
- ⚡ **Terminal Cepat BK (Retro CLI)**
  - Ketik perintah cepat seperti `list`, `stats`, `help`, hingga `reset` langsung dari konsol bawah aplikasi.
- 💾 **Offline-First & 100% Aman**
  - Menggunakan basis data lokal Room SQLite berkecepatan tinggi tanpa memerlukan koneksi internet lambat.

---

## 🛠️ Panduan Build & Rilis Otomatis (CI/CD GitHub Actions)

Repositori ini telah dilengkapi dengan file workflow `.github/workflows/build-apk.yml` untuk mem-build dan menandatangani (*sign*) APK Release secara otomatis setiap kali Anda membuat tag rilis baru (contoh: `v1.0.0`).

### 🔐 Konfigurasi GitHub Secrets untuk Keystore JKS:

1. Buat keystore rilis di komputer lokal (pastikan alias bernama **`upload`**):
   ```bash
   keytool -genkey -v -keystore release-keystore.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Konversi file keystore menjadi teks **Base64**:
   - **Linux / Windows Git Bash:**
     ```bash
     base64 -w 0 release-keystore.jks > keystore_base64.txt
     ```
   - **macOS:**
     ```bash
     base64 -i release-keystore.jks -o keystore_base64.txt
     ```
3. Buka repositori Anda di GitHub -> **Settings** -> **Secrets and variables** -> **Actions** -> **New repository secret**.
4. Tambahkan 3 secret berikut:
   - `KEYSTORE_BASE64` : Isi dengan seluruh teks dari file `keystore_base64.txt`.
   - `STORE_PASSWORD`  : Password keystore yang Anda tentukan saat `keytool`.
   - `KEY_PASSWORD`    : Password key alias yang Anda tentukan.

### 🚀 Memicu Rilis Otomatis:
Cukup buat tag rilis baru di git:
```bash
git tag v1.0.0
git push origin v1.0.0
```
GitHub Actions akan secara otomatis men-decode keystore, mengompilasi signed release APK, dan mempublikasikannya ke halaman **Releases** repositori Anda.

---

## 💻 Panduan Build Manual dari Source Code

Jika ingin mem-build aplikasi sendiri secara lokal menggunakan Android Studio:

```bash
# Clone repositori ini
git clone https://github.com/USERNAME/REPO_NAME.git
cd REPO_NAME

# Build APK Debug via Gradle
./gradlew assembleDebug

# Lokasi APK hasil build:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 🏫 Pengembang & Lisensi
Dikembangkan untuk **SMA NEGERI 1 Keritang**.
Hak Cipta © 2026. Bebas digunakan dan dikembangkan untuk kemajuan kedisiplinan pendidikan Indonesia.
