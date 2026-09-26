# Skenario Use Case RONDA

Dokumen ini memetakan alur kerja utama aplikasi RONDA beserta penanganan skenario alternatif (ketika kondisi tidak ideal atau ada *edge case* tertentu). Skenario ini didasarkan pada implementasi aktual dari *codebase* RONDA.

---

## UC01: Pendaftaran, Pairing, dan Manajemen Perangkat

**Skenario Normal: Pairing Pertama Kali (QR Code atau Input Kode)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna 1 membuka aplikasi RONDA, memilih peran "Rondor", dan mengisi nama Rondee yang ingin dipantau | Sistem membuat ID unik, menyimpannya di basis data lokal, dan menampilkan *QR Code* beserta *Kode Text* 6-digit (`RondorPairingScreen`). |
| 2 | Pengguna 2 membuka aplikasi RONDA, memilih "Rondee", lalu **memindai *QR Code*** atau **memasukkan *Kode Text*** secara manual | Sistem memvalidasi input, mengirim data ke Firebase (`PairingRepository`), menautkan perangkat, dan mengarahkan pengguna ke *Home Screen*. |

<br>

**Skenario Alternatif 1: Rondor Menambahkan Rondee Tambahan (Multi-Rondee)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondor membuka menu Pengaturan (*Settings*) dan menekan "Add Device" | Sistem memunculkan form untuk memasukkan nama perangkat Rondee baru (seperti pada tahap awal pengaturan). |
| 2 | Rondor mengisi nama lalu menekan tombol Lanjut | Sistem menerbitkan ID dan *QR Code*/*Kode Text* baru khusus untuk perangkat tambahan tersebut tanpa memutuskan koneksi dari Rondee sebelumnya. |
| 3 | Rondee baru memasukkan kode tersebut | Sistem menautkan Rondee baru ke akun Rondor. Menu *Settings* Rondor kini menampilkan daftar *Rondee connected* yang memuat dua perangkat atau lebih, dan beranda Rondor akan memantau notifikasi dari seluruh Rondee tersebut. |

<br>

**Skenario Alternatif 2: Memutus Koneksi (Disconnect / Unpair)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondor membuka menu Pengaturan, melihat daftar *Rondee connected*, lalu menekan tombol "Disconnect" pada salah satu spesifik Rondee | Sistem memunculkan prompt konfirmasi pemutusan koneksi khusus untuk Rondee yang dipilih. (Catatan: Rondee juga bisa menekan "Disconnect" mandiri dari sisi mereka). |
| 2 | Pengguna menekan tombol "Confirm" | Sistem menghapus sesi *pairing* tersebut, menghapus datanya dari perangkat lokal, dan mengirim pembaruan status pemutusan ke Firebase tanpa mengganggu koneksi ke Rondee lainnya (jika ada). |
| 3 | (Pada perangkat lawan / *the other party*) | Sistem mendeteksi putusnya koneksi secara *real-time* via Firebase, langsung memperbarui tampilan aplikasi, dan memunculkan notifikasi bahwa pihak sebelah telah memutus koneksi. |

<br>

**Skenario Alternatif 3: Kode Pairing / QR Code Tidak Valid**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" memindai QR Code atau mengetikkan kode yang salah/bukan dari sesi Rondor yang benar | Sistem (`QrCodeUtils`) gagal memvalidasi input, menolak proses *pairing*, menampilkan pesan kesalahan "Kode tidak valid", dan meminta pengguna mencoba lagi. |

<br>

**Skenario Alternatif 4: Kode Pairing Kedaluwarsa atau Sudah Dipakai**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" memasukkan kode yang dibuat Rondor lebih dari 10 menit lalu, atau kode yang sudah pernah diklaim perangkat lain | `PairingRepository.claimPairing()` membaca catatan *pairing* di Firebase dan menolak klaim (`Expired` / `AlreadyUsed`). Sistem menampilkan pesan spesifik: "Kode sudah kedaluwarsa. Minta kode baru." atau "Kode ini sudah dipakai. Minta kode baru." |
| 2 | Rondor membuat kode baru dan membacakannya ulang | Sistem menerbitkan kode baru yang berlaku 10 menit, lalu *pairing* dilanjutkan seperti Skenario Normal. |

<br>

**Skenario Alternatif 5: Tidak Ada Koneksi Internet Saat Pairing**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menekan tombol hubungkan saat HP tidak tersambung ke internet | Sistem gagal menjangkau Firebase (`ClaimResult.Failed`) dan menampilkan pesan "Tidak bisa terhubung ke server. Periksa koneksi internet, lalu coba lagi." Kode yang sudah diketik tetap tersimpan di kolom. |
| 2 | (Pada perangkat Rondor, jika pembuatan kode gagal) | `GuardianPairingScreen` menampilkan kartu gagal dengan tombol "Coba lagi" untuk menerbitkan ulang kode. |

<br>

**Skenario Alternatif 6: Memindai QR Code dengan Aplikasi Kamera (Deep Link)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" memindai *QR Code* Rondor memakai aplikasi kamera atau pemindai QR bawaan HP | QR berisi tautan `ronda://pair/XXXXXX`. OS membuka RONDA (atau meneruskan ke RONDA yang sedang terbuka lewat `onNewIntent`), dan `QrCodeUtils.codeFromLink()` mengambil kodenya. |
| 2 | (Sistem bekerja secara otomatis) | Kode diisikan ke kolom di `ProtectedPairingScreen` beserta keterangan bahwa kode sudah masuk tetapi belum terhubung. Jika Rondee masih di layar bahasa/peran, kode disimpan dan diisikan ketika layar *pairing* terbuka. |
| 3 | Pengguna "Rondee" menekan tombol hubungkan | *Pairing* dilanjutkan seperti Skenario Normal. Tautan tidak pernah memasangkan perangkat sendiri tanpa ketukan dari pemilik HP, agar persetujuan tetap terjadi secara sadar (PRD FR-2). |

---

## UC02: Mendeteksi Aplikasi Berbahaya (Initial Scan & Real-time)

**Skenario Normal 1: Initial Scan (Pasca-Pairing)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" baru saja menyelesaikan proses *pairing* (UC01) | Sistem (`DetectionService`) secara otomatis memicu *Initial Scan* untuk memindai seluruh aplikasi yang sudah ada di perangkat Rondee. |
| 2 | (Sistem bekerja secara otomatis) | `RiskEvaluator` memeriksa sumber instalasi dan izin setiap aplikasi. Jika ditemukan aplikasi *sideload* dengan izin berbahaya, sistem memvonis **HIGH RISK**. |
| 3 | (Sistem bekerja secara otomatis) | Sistem mencatat aplikasi bermasalah ke `FlaggedAppStore` dan mengirimkan peringatan massal (*Alerts*) ke perangkat Rondor via Firebase. |

<br>

**Skenario Normal 2: Deteksi Real-time Instalasi Baru (True Positive)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menginstal APK dari WhatsApp (di luar Play Store) | OS Android memicu *broadcast* `ACTION_PACKAGE_ADDED`. Sistem (melalui `InstallReceiver`) mulai mengeksekusi pemindaian di latar belakang. |
| 2 | (Sistem bekerja secara otomatis tanpa aksi pengguna) | `RiskEvaluator` mengekstrak data aplikasi. Karena meminta *permission* bahaya (misal `READ_SMS`), sistem memberikan vonis **HIGH RISK** dan mengirim *Alert* ke Rondor. |

<br>

**Skenario Alternatif 1: Scan Ulang Manual (Scan Again)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menekan tombol "Scan Again" pada halaman utama aplikasi | Sistem memulai ulang proses pemindaian ke seluruh aplikasi yang terinstal, mengulang siklus *Initial Scan*. |
| 2 | (Sistem bekerja secara otomatis) | Jika ada aplikasi berbahaya baru atau yang status *Safe*-nya dicabut, sistem akan memperbarui daftarnya dan segera melaporkan ke Rondor. |

<br>

**Skenario Alternatif 2: Instalasi Aplikasi Aman (True Negative / False Positive Handling)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menginstal aplikasi dari Play Store (meski butuh *permission* SMS), ATAU aplikasi *sideload* tanpa *permission* bahaya | `RiskEvaluator` mengeksekusi logika. Karena tidak memenuhi syarat bahaya, aplikasi divonis **Aman**. Sistem mencatatnya di `SafeAppStore` agar tidak ditanyakan lagi, dan **TIDAK** mengirim peringatan ke Rondor. |

<br>

**Skenario Alternatif 3: HP Rondee Offline Saat Aplikasi Berbahaya Terdeteksi**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menginstal APK berbahaya saat HP tidak tersambung ke internet | Deteksi tetap berjalan penuh di perangkat: `RiskEvaluator` memvonis **HIGH RISK**, sistem memunculkan notifikasi peringatan lokal, mencatat aplikasi di `FlaggedAppStore`, dan menyalakan *overlay* (UC03). |
| 2 | (Sistem bekerja secara otomatis) | Penulisan *alert* ke Firebase gagal terkonfirmasi dalam 8 detik. *Persistence* Firebase Realtime Database menyimpan *alert* di antrean lokal. |
| 3 | HP Rondee kembali tersambung ke internet | Firebase mengirim *alert* yang tertunda, dan perangkat Rondor menerima peringatan seperti pada Skenario Normal 2. |

<br>

**Skenario Alternatif 4: Aplikasi yang Sudah Ditandai Diperbarui (Update)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Aplikasi yang sudah masuk daftar **HIGH RISK** diperbarui ke versi baru | OS mengirim `ACTION_PACKAGE_REMOVED` (dengan `EXTRA_REPLACING`) lalu `ACTION_PACKAGE_ADDED`. `InstallReceiver` mengabaikan sinyal *removed* karena aplikasi sebenarnya masih terpasang, sehingga status **HIGH RISK** dan pemblokiran tetap berlaku. |

<br>

**Skenario Alternatif 5: Aplikasi yang Pernah Ditandai Aman Diinstal Ulang**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menghapus aplikasi yang sebelumnya ditandai aman oleh Rondor | `InstallReceiver` menghapus aplikasi tersebut dari `SafeAppStore`, karena instalasi ulang dianggap sebagai pertanyaan baru. |
| 2 | Pengguna "Rondee" menginstal aplikasi dengan nama paket yang sama lagi | Aplikasi dinilai ulang dari awal oleh `RiskEvaluator`. Jika hasilnya **HIGH RISK**, Rondor kembali menerima peringatan. |

---

## UC03: Pemblokiran Aplikasi Berbahaya (Soft-block Overlay)

**Skenario Normal: Overlay Muncul Saat Aplikasi Dibuka**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" mencoba membuka aplikasi yang baru saja masuk daftar **HIGH RISK** | Sistem (`ForegroundAppMonitor`) mendeteksi paket tersebut berada di *foreground*. |
| 2 | (Sistem bekerja secara otomatis) | `OverlayService` langsung menggambar peringatan layar penuh di atas aplikasi. Sistem tidak menyediakan tombol tutup; pengguna hanya bisa menekan "Home" pada OS untuk keluar. |

<br>

**Skenario Alternatif 1: Aplikasi Telah Ditandai Aman oleh Rondor**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" membuka aplikasi yang sebelumnya diblokir, namun telah diizinkan (*Mark as Safe*) oleh Rondor | `ForegroundAppMonitor` mengecek statusnya di basis data, menemukan status "Aman", sehingga *overlay* ditahan. Pengguna dapat memakai aplikasi dengan normal. |

<br>

**Skenario Alternatif 2: Izin Overlay atau Usage Access Belum Diberikan / Dicabut**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Aplikasi **HIGH RISK** terdeteksi, tetapi izin `SYSTEM_ALERT_WINDOW` atau `PACKAGE_USAGE_STATS` belum diberikan, atau dicabut oleh *power management* OEM | `Permissions.canBlock()` bernilai *false*. *Overlay* tidak dijalankan, atau `OverlayService` berhenti sendiri jika izin dicabut saat sedang berjalan. Deteksi dan pengiriman *alert* ke Rondor tetap berjalan normal. |
| 2 | Pengguna "Rondee" membuka RONDA | Beranda Rondee berubah menjadi kartu kuning dengan tombol "Lanjutkan pengaturan" yang membawa ke *wizard* izin (UC05). |
| 3 | Pengguna "Rondee" memberikan izin kembali, lalu kembali ke RONDA | Sistem memeriksa ulang izin saat `onResume`. Karena masih ada aplikasi di `FlaggedAppStore`, `OverlayService` dinyalakan lagi dan pemblokiran kembali aktif. |

<br>

**Skenario Alternatif 3: HP Rondee Di-restart**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | HP Rondee dinyalakan ulang (atau proses RONDA dimatikan OS) saat masih ada aplikasi **HIGH RISK** yang belum ditangani | Daftar aplikasi bermasalah tetap tersimpan di `FlaggedAppStore` pada penyimpanan lokal. |
| 2 | Pengguna "Rondee" membuka RONDA | `MainActivity.refreshStatus()` menyalakan kembali `DetectionService` dan, jika izin lengkap, `OverlayService`, sehingga aplikasi berbahaya kembali tertutup *overlay*. |

<br>

**Skenario Alternatif 4: Pengguna Menekan Tombol Back di Overlay**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menekan tombol Back atau mengetuk layar saat *overlay* tampil | *Overlay* menyerap sentuhan dan tombol Back sehingga keduanya tidak diteruskan ke aplikasi berbahaya di bawahnya. Satu-satunya jalan keluar tetap tombol Home. |

---

## UC04: Penanganan Peringatan oleh Rondor & Sinkronisasi Uninstall

**Skenario Normal: Rondor Memicu Uninstall dan Status Sinkron**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondor membuka notifikasi FCM yang masuk, sistem menampilkan layar rincian aplikasi berbahaya (`AlertDetailScreen`) | Sistem mendisplay nama paket, sumber, dan alasan kenapa aplikasi tersebut berbahaya. |
| 2 | Rondor menekan tombol "Uninstall" | Sistem mengirim *Command Uninstall* ke Firebase. |
| 3 | (Pada perangkat "Rondee") | `CommandHandler` menerima instruksi, lalu mengeksekusi `Intent.ACTION_DELETE`. OS Android memunculkan *pop-up* sistem yang meminta pengguna "Rondee" mengonfirmasi *uninstall*. |
| 4 | Pengguna "Rondee" menekan "OK" pada dialog sistem Android | Aplikasi terhapus. OS menyebarkan `ACTION_PACKAGE_REMOVED`. |
| 5 | (Sistem bekerja secara otomatis) | `InstallReceiver` menangkap pelepasan aplikasi, mengubah status di Firebase menjadi `STATUS_UNINSTALLED`, dan menghapus pemblokiran lokal. |
| 6 | (Pada perangkat Rondor) | Layar Rondor otomatis terbarui, memindahkan *alert* tersebut dari daftar aktif ke tab *History* (Riwayat), dan `RondorAlertService` memunculkan notifikasi sukses: "Aplikasi [Nama] telah berhasil dihapus dari perangkat Rondee". |

<br>

**Skenario Alternatif 1: Rondor Membiarkan (Tandai Aman)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondor menekan tombol "Tandai Aman (*Mark as Safe*)" pada `AlertDetailScreen` | Sistem mengirim *Command Safe* ke Firebase. |
| 2 | (Pada perangkat "Rondee") | `CommandHandler` menerima instruksi, menghapus aplikasi dari `FlaggedAppStore`, memindahkannya ke `SafeAppStore`, dan segera mencabut *Overlay* jika sedang aktif. |
| 3 | (Pada perangkat Rondor) | Sistem memperbarui status *alert* menjadi selesai (aman), memindahkannya ke tab *History*, dan memunculkan notifikasi bahwa peringatan telah berhasil ditandai aman. |

<br>

**Skenario Alternatif 2: Pengguna "Rondee" Batal Melakukan Uninstall**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menekan "Cancel" atau menunda (*defer*) *pop-up* sistem Android saat instruksi *uninstall* dari Rondor masuk | OS membatalkan penghapusan. `MainActivity` menahan *pending uninstall*. Aplikasi tetap berada dalam daftar **HIGH RISK**, *overlay* tetap aktif jika aplikasi dibuka, dan status di layar Rondor akan tetap "Menunggu aksi" sampai *uninstall* benar-benar berhasil dieksekusi. |

<br>

**Skenario Alternatif 3: Rondee Menunda Permintaan Uninstall di Layar RONDA ("Nanti saja")**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Permintaan *uninstall* dari Rondor masuk saat HP Rondee sedang tidak dipegang | `CommandHandler` menyimpan permintaan di `PendingUninstallStore` dan memunculkan notifikasi prioritas tinggi "Penjaga Anda minta aplikasi ini dihapus". |
| 2 | Pengguna "Rondee" membuka RONDA (atau mengetuk notifikasi) | `UninstallPromptScreen` mengambil alih layar dan menjelaskan dengan bahasa sederhana bahwa penjaga meminta aplikasi dihapus. |
| 3 | Pengguna "Rondee" menekan "Nanti saja" | Sistem kembali ke beranda Rondee. Permintaan tetap tersimpan, aplikasi tetap diblokir *overlay*, dan status di layar Rondor tetap menunggu. |

<br>

**Skenario Alternatif 4: Rondor Mengirim Ulang Permintaan Uninstall**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondor membuka `AlertDetailScreen` untuk aplikasi yang sudah diminta dihapus tetapi belum juga dihapus oleh Rondee | Sistem menampilkan status bahwa aplikasi masih menunggu dihapus di HP Rondee, beserta tombol "Kirim lagi permintaannya". |
| 2 | Rondor menekan "Kirim lagi permintaannya" | Sistem mengirim *Command Uninstall* baru ke Firebase. Perangkat Rondee kembali memunculkan notifikasi permintaan dan alur berlanjut seperti Skenario Normal langkah 3. |

<br>

**Skenario Alternatif 5: Rondor Membatalkan "Tandai Aman" (Undo)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondor menekan "Tandai Aman" pada `AlertDetailScreen` | Sistem memunculkan lembar konfirmasi karena menandai aman berarti mencabut perlindungan dari HP Rondee. |
| 2 | Rondor menekan konfirmasi | Sistem mengirim *Command Safe* (seperti Skenario Alternatif 1) dan menampilkan tombol "Batalkan" selama 10 detik. |
| 3 | Rondor menekan "Batalkan" dalam 10 detik | Sistem mengubah status *alert* kembali menjadi tidak aman (`STATUS_UNSAFE`) sehingga Rondor dapat memilih "Uninstall". |

---

## UC05: Setup Awal dan Izin Perangkat

**Skenario Normal: Onboarding Pertama Kali**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna membuka RONDA untuk pertama kali | Sistem menampilkan *splash screen* lalu layar pemilihan bahasa (Indonesia / English). |
| 2 | Pengguna memilih bahasa dan menekan "Lanjutkan" | Sistem menerapkan bahasa tersebut ke seluruh aplikasi, lalu menampilkan layar pengenalan singkat (`IntroScreen`). |
| 3 | Pengguna menekan tombol mulai | Sistem menampilkan layar pemilihan peran: "HP saya sebagai penjaga" (Rondor) atau "HP orang tua saya" (Rondee). |
| 4 | Pengguna memilih peran | Sistem menyimpan peran secara lokal (`RoleStore`). Jika Rondor, sistem langsung meminta izin notifikasi. Jika Rondee, sistem menyalakan `DetectionService`. Keduanya lalu diarahkan ke alur *pairing* (UC01). |

<br>

**Skenario Normal 2: Wizard Izin di HP Rondee**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondee baru selesai *pairing* dan izin belum lengkap | Sistem menampilkan `SetupWizardScreen` berisi 4 langkah, satu izin per layar: notifikasi, tampil di atas aplikasi lain (`SYSTEM_ALERT_WINDOW`), akses penggunaan (`PACKAGE_USAGE_STATS`), dan pengecualian optimasi baterai. Setiap langkah menjelaskan alasan izin tersebut dibutuhkan. |
| 2 | Pengguna (dibantu Rondor) menekan "Aktifkan" | Sistem membuka dialog izin atau halaman *Settings* Android yang sesuai. |
| 3 | Pengguna memberikan izin lalu kembali ke RONDA | Sistem memeriksa ulang semua izin saat `onResume`, mengisi indikator progres, dan menggeser ke langkah berikutnya. |
| 4 | Semua izin sudah diberikan, pengguna menekan "Selesai" | Sistem menampilkan beranda Rondee dengan kartu hijau "terlindungi" dan nama penjaganya. |

<br>

**Skenario Alternatif 1: Pengguna Keluar dari Wizard Sebelum Selesai**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menekan tombol Back di tengah *wizard* | Sistem kembali ke beranda Rondee yang menampilkan kartu kuning berisi izin yang belum aktif, beserta tombol "Lanjutkan pengaturan". Deteksi dan pengiriman *alert* tetap berjalan, tetapi *overlay* belum bisa memblokir (UC03 Skenario Alternatif 2). |
| 2 | Pengguna menekan "Lanjutkan pengaturan" | Sistem membuka kembali *wizard* pada langkah pertama yang belum selesai. |

<br>

**Skenario Alternatif 2: Rondee Melihat Siapa yang Menjaga (Transparansi Pemantauan)**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Pengguna "Rondee" menarik panel notifikasi | Sistem selalu menampilkan notifikasi permanen dari `DetectionService` yang menyatakan RONDA sedang memantau HP ini. |
| 2 | Pengguna "Rondee" menekan ikon profil di beranda | Sistem membuka lembar informasi berisi nama penjaga dan kode *pairing*. Lembar ini hanya menampilkan informasi, tanpa tombol yang bisa mengubah atau memutus *pairing* (PRD §5.5). |

---

## UC06: Pengaturan Aplikasi Rondor

**Skenario Normal: Mengubah Nama Panggilan, Tema, dan Bahasa**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | Rondor membuka tab "Setelan" di bilah navigasi bawah | Sistem menampilkan pengaturan HP yang dijaga, tampilan, bahasa, privasi, dan opsi memutus *pairing*. |
| 2 | Rondor mengubah nama panggilan Rondee (misal "Ibu") | Sistem menyimpan nama baru secara lokal. Semua kalimat di HP Rondor (daftar peringatan, rincian, notifikasi) langsung memakai nama tersebut. |
| 3 | Rondor mengganti tema (terang / gelap / ikuti sistem) atau bahasa | Sistem menyimpan pilihan dan menerapkannya ke seluruh aplikasi. |

<br>

**Skenario Alternatif 1: Rondor Sedang Offline**

| No | Aksi Aktor | Reaksi Perangkat Lunak |
| :--- | :--- | :--- |
| 1 | HP Rondor kehilangan koneksi internet saat membuka tab Peringatan | Sistem membaca status koneksi Firebase (`.info/connected`) dan menampilkan penanda offline di bilah atas, agar daftar kosong tidak disalahartikan sebagai "semua aman". |
| 2 | Koneksi kembali | Penanda offline hilang, dan *alert* yang masuk selama offline langsung tampil di daftar. |
