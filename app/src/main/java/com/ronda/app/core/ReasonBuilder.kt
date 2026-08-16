package com.ronda.app.core

/**
 * Turns signal keys into plain-Indonesian sentences.
 *
 * The score alone is useless to a non-technical guardian — someone who sees "96"
 * still does not know what to do. Every sentence here names the concrete
 * consequence to the reader, not the technical capability.
 *
 * Writing rules:
 *  - zero jargon (no "eksfiltrasi", "payload", "privilege escalation")
 *  - state irreversibility where it applies
 *  - do not exaggerate low-severity signals; if RONDA cries wolf on ordinary
 *    apps, guardians stop reading and the whole mechanism fails
 *
 * Kept as a Kotlin map rather than `res/raw/capabilities.yaml` so it stays in one
 * file with no YAML parser dependency and no `Context` reaching into the core
 * package. Markdown `**bold**` is rendered by the UI layer.
 */
object ReasonBuilder {

    private val STRINGS: Map<String, String> = mapOf(
        "ACCESSIBILITY" to
            "Aplikasi ini bisa **melihat dan mengontrol apa pun** yang kamu lakukan di layar — " +
            "termasuk mengetik sendiri, menekan tombol sendiri, dan membaca isi aplikasi lain.",
        "DEVICE_ADMIN" to
            "Aplikasi ini meminta hak **administrator perangkat**, yang membuatnya " +
            "**sangat sulit dihapus** dan bisa mengunci atau menghapus isi HP kamu.",
        "SMS_READ" to
            "Aplikasi ini bisa membaca **semua SMS** kamu, termasuk **kode OTP dari bank**. " +
            "Dengan itu, orang lain bisa masuk ke rekening kamu tanpa perlu tahu passwordmu.",
        "INSTALL_PKG" to
            "Aplikasi ini bisa **memasang aplikasi lain** ke HP kamu, bahkan tanpa kamu minta.",
        "OVERLAY" to
            "Aplikasi ini bisa **menampilkan layar palsu di atas aplikasi lain** — " +
            "misalnya halaman login bank tiruan untuk mencuri passwordmu.",
        "NOTIF_LISTENER" to
            "Aplikasi ini bisa **membaca semua notifikasi** yang masuk, termasuk isi SMS " +
            "dan kode OTP yang muncul di layar kunci.",
        "AUDIO" to
            "Aplikasi ini bisa **merekam suara** lewat mikrofon HP kamu, termasuk saat kamu " +
            "sedang menelepon.",
        "CALL" to
            "Aplikasi ini bisa **menelepon sendiri** dan mengangkat panggilan, termasuk ke " +
            "nomor berbayar yang memotong pulsamu.",
        "CONTACTS" to
            "Aplikasi ini bisa **membaca seluruh daftar kontakmu** dan mengirimnya ke orang lain. " +
            "Nomor keluargamu bisa ikut dipakai untuk menipu mereka.",
        "CAMERA" to
            "Aplikasi ini bisa **mengambil foto dan video** lewat kamera HP kamu.",
        "LOCATION" to
            "Aplikasi ini bisa **mengetahui lokasimu** secara tepat, kapan pun.",
        "PHONE_STATE" to
            "Aplikasi ini bisa membaca **nomor dan identitas HP kamu**.",
        "QUERY_PKGS" to
            "Aplikasi ini bisa **melihat daftar semua aplikasi** yang terpasang di HP kamu, " +
            "termasuk aplikasi bank apa saja yang kamu pakai.",
        "BOOT" to
            "Aplikasi ini **otomatis menyala sendiri** setiap kali HP kamu dihidupkan.",
        "INTERNET" to
            "Aplikasi ini bisa **mengirim data keluar** lewat internet.",
        "FG_SERVICE" to
            "Aplikasi ini bisa **terus berjalan di latar belakang** walaupun tidak kamu buka.",

        "SRC_PLAY" to
            "Aplikasi ini dipasang dari **Play Store** dan sudah melewati pemeriksaan keamanan Google.",
        "SRC_KNOWN_STORE" to
            "Aplikasi ini dipasang dari **toko aplikasi lain**, bukan Play Store.",
        "SRC_SIDELOAD" to
            "Aplikasi ini **tidak dipasang dari Play Store**. Biasanya dikirim lewat WhatsApp " +
            "atau link, dan tidak melewati pemeriksaan keamanan Google.",
        "CERT_SELF_SIGNED" to
            "Aplikasi ini **tidak punya tanda tangan resmi** dari perusahaan mana pun, " +
            "jadi tidak ada yang bisa dimintai tanggung jawab kalau terjadi apa-apa.",
        "NO_LAUNCHER" to
            "Aplikasi ini **menyembunyikan ikonnya**, jadi kamu tidak akan sadar aplikasi ini " +
            "terpasang di HP kamu.",
        "LEGACY_SDK" to
            "Aplikasi ini dibuat untuk **Android versi lama**, sehingga bisa melewati " +
            "perlindungan yang ada di HP kamu sekarang.",
        "NAME_MIMIC" to
            "Nama aplikasi ini **meniru aplikasi resmi** (bank atau dompet digital). " +
            "Aplikasi asli tidak pernah dikirim lewat WhatsApp."
    )

    /**
     * Reasons for the active signals, ordered most severe first.
     *
     * Impact signals rank by weight; trust signals rank by how far their
     * multiplier departs from neutral, which puts sideloading and a hidden icon
     * among the mid-weight capabilities and sinks the reassuring Play Store note
     * to the bottom.
     */
    fun build(active: List<Signal>): List<String> = active
        .sortedWith(compareByDescending<Signal> { it.severity }.thenBy { it.key })
        .mapNotNull { STRINGS[it.key] }
}
