package com.ronda.testsample

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Stub aksesibilitas yang sepenuhnya inert.
 *
 * Dideklarasikan HANYA supaya mesin deteksi RONDA membaca sinyal
 * BIND_ACCESSIBILITY_SERVICE lewat PackageManager.GET_SERVICES. Tanpa
 * intent-filter aksesibilitas dan tanpa meta-data konfigurasi, layanan ini
 * tidak pernah muncul di Setelan dan tidak bisa diaktifkan. Kedua metode di
 * bawah sengaja dibiarkan kosong — tidak ada satu pun peristiwa yang dibaca
 * atau input yang disentuh.
 */
class AccessibilityStubService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) { /* sengaja kosong */ }
    override fun onInterrupt() { /* sengaja kosong */ }
}
