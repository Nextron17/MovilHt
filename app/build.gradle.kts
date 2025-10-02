plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services) // <-- AÑADE ESTA LÍNEA
}

android {
    namespace = "com.example.hortitechv1"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.hortitechv1"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.recyclerview)
    implementation(libs.cardview)

    implementation(libs.github.glide)
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0") // <-- Importante para Glide

    // Logging de Red
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // --- DEPENDENCIAS DE FIREBASE AÑADIDAS ---
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging.ktx)
}