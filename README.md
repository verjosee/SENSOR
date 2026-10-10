Aplikasi GARIS / JARAK menggunakan dua sensor Android untuk membuat tampilan aplikasi bisa merespons gerakan HP dan jarak benda di sekitar sensor.
- Tipe: Sensor.TYPE_ACCELEROMETER
- Kategori: Motion (Gerakan)
- Fungsi: Mengukur percepatan pada sumbu X, Y, dan Z.
- Penggunaan: Sensor ini digunakan untuk menggerakkan objek di layar sesuai kemiringan HP.
Cara mencoba: Miringkan HP ke kiri, kanan, depan, atau belakang. Objek di layar akan bergerak mengikuti kemiringan HP.
- Tipe: Sensor.TYPE_PROXIMITY
- Kategori: Position (Kedekatan)
- Fungsi: Mendeteksi benda yang berada dekat dengan sensor.
- Penggunaan: Sensor ini digunakan untuk mengubah tampilan aplikasi saat tangan didekatkan atau dijauhkan dari sensor.
Cara mencoba: Dekatkan tangan ke bagian atas HP, lalu jauhkan kembali. Lihat perubahan tampilan pada aplikasi.
- Kotlin
- Jetpack Compose
- Android Sensor Framework
Catatan: Hasil pembacaan sensor bergantung pada perangkat yang digunakan.

