plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.ksra_handygo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ksra_handygo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 👇 Important for AppAuth redirect URI
        manifestPlaceholders["appAuthRedirectScheme"] = "ksrafisherman"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // OAuth2 / OpenID Connect support
    implementation("net.openid:appauth:0.11.1")
}
