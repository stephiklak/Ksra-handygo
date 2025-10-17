plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.ksra_handygo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ksra_handygo"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        // manifestPlaceholders can be used if you prefer
        // manifestPlaceholders["appAuthRedirectScheme"] = "ksrafisherman"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["appAuthRedirectScheme"] = "ksrafisherman"
    }

    buildFeatures { }
    compileOptions { sourceCompatibility = JavaVersion.VERSION_11; targetCompatibility = JavaVersion.VERSION_11 }
    kotlinOptions { jvmTarget = "11" }
}


dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.10.0")

    // AppAuth for OAuth2/OIDC
    implementation("net.openid:appauth:0.11.1")
}
