package com.ronda.app

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

enum class ThemeMode { SYSTEM, LIGHT, DARK }

enum class AppLanguage(val tag: String) {
    INDONESIAN("id"),
    ENGLISH("en");

    companion object {
        /**
         * What the app is currently speaking. An empty per-app locale list means
         * "follow the phone", so the phone's language decides the radio.
         */
        fun current(): AppLanguage {
            val tags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            val language = if (tags.isBlank()) Locale.getDefault().language else tags
            return if (language.startsWith("en")) ENGLISH else INDONESIAN
        }
    }
}

/**
 * Returns a localized Context respecting the active AppLanguage.
 * Ensures notifications and services use the selected language even on older Android versions.
 */
fun Context.localized(): Context {
    val language = AppLanguage.current()
    val locale = Locale(language.tag)
    Locale.setDefault(locale)
    val config = android.content.res.Configuration(resources.configuration)
    config.setLocale(locale)
    return createConfigurationContext(config)
}

/**
 * First-run flags and the two preferences the guardian can change in Setelan.
 *
 * Theme and language are both applied through AppCompatDelegate rather than
 * kept in Compose state: the delegate rewrites the Activity's configuration,
 * so `isSystemInDarkTheme()`, every `-night` resource (the overlay, the window
 * background) and every string follow one switch and survive process death.
 */
class SettingsStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var themeMode: ThemeMode
        get() = prefs.getString(KEY_THEME, null)?.let(ThemeMode::valueOf) ?: ThemeMode.SYSTEM
        set(value) {
            prefs.edit().putString(KEY_THEME, value.name).apply()
            applyTheme()
        }

    /** The intro screen is shown once per install. */
    var introSeen: Boolean
        get() = prefs.getBoolean(KEY_INTRO_SEEN, false)
        set(value) = prefs.edit().putBoolean(KEY_INTRO_SEEN, value).apply()

    /** The language screen is shown once per install, before anything else. */
    var languageChosen: Boolean
        get() = prefs.getBoolean(KEY_LANGUAGE_CHOSEN, false)
        set(value) = prefs.edit().putBoolean(KEY_LANGUAGE_CHOSEN, value).apply()

    var initialScanDone: Boolean
        get() = prefs.getBoolean(KEY_INITIAL_SCAN_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_INITIAL_SCAN_DONE, value).apply()

    /** Called at process start so the first frame already has the right theme. */
    fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            when (themeMode) {
                ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            }
        )
    }

    /**
     * The flag is written before the locale changes on purpose: on API 30–32
     * the delegate recreates the Activity immediately, and the language screen
     * must not come back after the recreation.
     */
    fun setLanguage(language: AppLanguage) {
        languageChosen = true
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
    }

    private companion object {
        const val PREFS_NAME = "ronda_settings"
        const val KEY_THEME = "theme_mode"
        const val KEY_INTRO_SEEN = "intro_seen"
        const val KEY_LANGUAGE_CHOSEN = "language_chosen"
        const val KEY_INITIAL_SCAN_DONE = "initial_scan_done"
    }
}
