package com.ronda.testsample

import android.service.notification.NotificationListenerService

/**
 * Stub notification-listener yang inert.
 *
 * Dideklarasikan HANYA supaya RONDA membaca sinyal
 * BIND_NOTIFICATION_LISTENER_SERVICE. Tidak ada intent-filter listener, jadi
 * sistem tidak pernah mengikatnya dan tidak ada satu notifikasi pun yang
 * dibaca. Kelas kosong dengan sengaja.
 */
class NotifStubService : NotificationListenerService()
