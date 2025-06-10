# CineSpot

CineSpot adalah aplikasi Android yang memungkinkan pengguna menjelajahi daftar film, mencari informasi detail berdasarkan judul, serta menyimpan film favorit pengguna. Aplikasi ini terhubung ke API IMDB melalui RapidAPI untuk memberikan informasi yang akurat dan terkini. CineSpot juga mendukung pengaturan tema gelap/terang dan fitur akun untuk menyimpan preferensi pengguna.

## Fitur
- Pencarian Film berdasarkan judul.
- Tambahkan ke Favorit untuk menyimpan film yang disukai.
- Dukungan Tema Terang/Gelap yang bisa disimpan secara lokal.
- Manajemen Akun Pengguna (login & penyimpanan data pengguna).
- Penyimpanan Favorit Lokal dengan SQLite.

## Penggunaan
- Luncurkan aplikasi CineSpot di perangkat Android Anda.
- Buat akun baru atau login dengan akun yang sudah ada.
- Gunakan tab navigasi bawah untuk:
  1. **Home**: Lihat daftar film dan gunakan fitur pencarian.
  2. **Favorite**: Lihat film favorit yang telah disimpan.
  3. **Profile**: Atur akun dan ganti tema tampilan.
- Klik film untuk melihat detailnya.
- Tekan ikon favorit untuk menyimpan atau menghapus dari daftar favorit.

## Implementasi Teknis

CineSpot dikembangkan sebagai aplikasi Android berbasis Java yang mengintegrasikan berbagai komponen untuk menciptakan pengalaman menjelajah film.

CineSpot menyediakan dukungan tema terang dan gelap. Pengaturan tema ini tidak hanya diterapkan secara real-time tetapi juga disimpan secara lokal menggunakan `SharedPreferences`, sehingga saat aplikasi dibuka kembali, preferensi tema pengguna tetap terjaga.

CineSpot mengintegrasikan API dari IMDB melalui layanan RapidAPI. Pengambilan data film dilakukan menggunakan Retrofit.

Untuk pengelolaan data lokal, terutama terkait akun pengguna dan daftar film favorit, CineSpot menggunakan SQLite.

Proses penambahan, penghapusan, dan pengecekan film favorit dilakukan melalui metode yang secara efisien menggunakan query SQL dan pengelolaan koneksi database. Dengan pendekatan ini, aplikasi mampu berjalan secara offline dalam hal pengelolaan favorit, dan hanya membutuhkan koneksi saat mengakses data dari IMDB.

