package com.ronda.testsample

import android.app.admin.DeviceAdminReceiver

/**
 * Stub device-admin yang inert.
 *
 * Dideklarasikan HANYA supaya RONDA membaca sinyal BIND_DEVICE_ADMIN lewat
 * PackageManager.GET_RECEIVERS. Tanpa meta-data kebijakan (<meta-data
 * android:name="android.app.device_admin">), sistem menolak mengaktifkannya
 * sebagai admin perangkat, jadi receiver ini tidak punya kuasa apa pun.
 */
class AdminStubReceiver : DeviceAdminReceiver()
