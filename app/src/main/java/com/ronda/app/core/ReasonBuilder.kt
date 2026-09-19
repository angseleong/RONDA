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

    private val STRINGS_ID: Map<String, String> = mapOf(
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

    private val STRINGS_EN: Map<String, String> = mapOf(
        "ACCESSIBILITY" to
            "This app can **view and control everything** you do on screen — " +
            "including typing, clicking buttons, and reading other apps.",
        "DEVICE_ADMIN" to
            "This app requests **device administrator** privileges, making it " +
            "**very hard to uninstall** and able to lock or erase your device.",
        "SMS_READ" to
            "This app can read **all your SMS messages**, including **bank OTP codes**. " +
            "Attackers could access your accounts without needing your password.",
        "INSTALL_PKG" to
            "This app can **install other apps** onto your phone without your knowledge.",
        "OVERLAY" to
            "This app can **draw fake screens on top of other apps** — " +
            "for instance, a fake bank login to steal credentials.",
        "NOTIF_LISTENER" to
            "This app can **read all incoming notifications**, including SMS text " +
            "and OTP codes on your lock screen.",
        "AUDIO" to
            "This app can **record audio** through your microphone, including during calls.",
        "CALL" to
            "This app can **make and answer phone calls** on its own, including to paid numbers.",
        "CONTACTS" to
            "This app can **read your entire contacts list** and send it away, exposing your family to scams.",
        "CAMERA" to
            "This app can **take photos and record videos** using your camera.",
        "LOCATION" to
            "This app can **track your exact location** at any time.",
        "PHONE_STATE" to
            "This app can read **your phone number and hardware identity**.",
        "QUERY_PKGS" to
            "This app can **see all apps installed on your device**, including banking applications.",
        "BOOT" to
            "This app **starts automatically** every time your phone turns on.",
        "INTERNET" to
            "This app can **transmit data out** over the internet.",
        "FG_SERVICE" to
            "This app can **run continuously in the background** even when closed.",

        "SRC_PLAY" to
            "This app was installed from **Google Play** and passed Google security verification.",
        "SRC_KNOWN_STORE" to
            "This app was installed from **another app store**, not Google Play.",
        "SRC_SIDELOAD" to
            "This app was **not installed from Google Play**. Sideloaded apps bypass Google security verification.",
        "CERT_SELF_SIGNED" to
            "This app has **no verified signature** from a recognized developer or publisher.",
        "NO_LAUNCHER" to
            "This app **hides its icon**, so you will not see it on your home screen.",
        "LEGACY_SDK" to
            "This app targets an **outdated Android version**, bypassing modern privacy protections.",
        "NAME_MIMIC" to
            "The app name **mimics an official application** (like a bank or wallet)."
    )

    /**
     * Reasons for the active signals, ordered most severe first.
     */
    fun build(active: List<Signal>, isEnglish: Boolean = com.ronda.app.AppLanguage.current() == com.ronda.app.AppLanguage.ENGLISH): List<String> {
        val strings = if (isEnglish) STRINGS_EN else STRINGS_ID
        return active
            .sortedWith(compareByDescending<Signal> { it.severity }.thenBy { it.key })
            .mapNotNull { strings[it.key] }
    }
}
