package com.ronda.testsample

import android.app.Activity
import android.os.Bundle

/**
 * Sampel uji RONDA.
 *
 * Aplikasi ini sengaja tidak melakukan apa pun selain menampilkan satu layar statis.
 * Tidak ada permintaan permission saat runtime, tidak ada BroadcastReceiver,
 * tidak ada Service, tidak ada akses jaringan, tidak ada penulisan file.
 *
 * Satu-satunya hal yang membuatnya "menarik" bagi RONDA adalah deklarasi
 * READ_SMS di AndroidManifest.xml — sinyal yang dibaca mesin deteksi lewat
 * PackageManager.getPackageInfo(..., GET_PERMISSIONS).
 */
class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
