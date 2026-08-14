# RONDA
Hacknusa

## Demo cepat (dua emulator)

Simulasi penuh dari nol — nyalakan emulator, pasang APK, reset ke layar pilih
peran, pairing, lalu sideload APK umpan yang mendeklarasikan `READ_SMS`:

```bash
scripts/ronda demo
scripts/ronda victim   # buka APK umpan → overlay RONDA menutupinya
```

Peran ditentukan dari nama AVD: `Pixel_6` jadi HP orang tua (PROTECTED),
`RONDA_Guardian` jadi HP penjaga (GUARDIAN). Ganti lewat env
`RONDA_PROTECTED_AVD` / `RONDA_GUARDIAN_AVD`.

Perintah lain: `up`, `install`, `reset`, `perms`, `open`, `role`, `pair`,
`attack [installer]`, `status`, `logs`, `shot`. Lihat `scripts/ronda help`.

Untuk membuktikan aturan dua sinyal (sideload **dan** SMS), pasang APK umpan
seolah-olah dari Play Store — hasilnya `risk=LOW`, tanpa alert:

```bash
scripts/ronda attack com.android.vending
```
