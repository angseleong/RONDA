plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.ronda.testsample"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ronda.testsample"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Each flavor is a separate harmless decoy that declares ONE group of RONDA
    // detection signals and nothing else. They share the same UI and code; only
    // their AndroidManifest + app label differ. None of them ever act on the
    // permissions they declare — see the per-flavor manifest comments.
    //
    // Expected RONDA scores (sideloaded via adb, self-signed debug cert):
    //   sms           READ_SMS + INTERNET                     -> 85  PERINGATAN
    //   accessibility BIND_ACCESSIBILITY_SERVICE + OVERLAY    -> 100 DARURAT
    //   notification  BIND_NOTIFICATION_LISTENER + INTERNET   -> 70  PERINGATAN
    //   overlay       SYSTEM_ALERT_WINDOW only                -> 43  RENDAH (no alert)
    //   deviceadmin   BIND_DEVICE_ADMIN + hidden launcher     -> 88  PERINGATAN
    //   dropper       REQUEST_INSTALL_PACKAGES + INTERNET     -> 63  PERINGATAN
    flavorDimensions += "signal"
    productFlavors {
        create("sms") {
            dimension = "signal"
            // No suffix: stays com.ronda.testsample so scripts/ronda still works.
        }
        create("accessibility") {
            dimension = "signal"
            applicationIdSuffix = ".accessibility"
        }
        create("notification") {
            dimension = "signal"
            applicationIdSuffix = ".notification"
        }
        create("overlay") {
            dimension = "signal"
            applicationIdSuffix = ".overlay"
        }
        create("deviceadmin") {
            dimension = "signal"
            applicationIdSuffix = ".deviceadmin"
        }
        create("dropper") {
            dimension = "signal"
            applicationIdSuffix = ".dropper"
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}