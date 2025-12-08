plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Plugin de Google Services
    id("com.google.gms.google-services")
}

android {
    namespace = "com.david.maizly" // <-- Corregido a tu namespace
    compileSdk = 36

    defaultConfig {
        applicationId = "com.david.maizly" // <-- Corregido
        minSdk = 26
        targetSdk = 36
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
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // Tu 'packaging' (buena práctica para evitar conflictos)
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // --- DEPENDENCIAS DE FIREBASE (Tus versiones) ---
    implementation("com.google.firebase:firebase-common-ktx:21.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.0.0")
    implementation("com.google.firebase:firebase-analytics-ktx:22.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.0.0")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.0")

    // --- DEPENDENCIA ESENCIAL PARA VIEWMODEL ---
    // La necesitamos para manejar la lógica de Firebase (el "cerebro")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Navegación en Compose (ya la tenías)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Coil (la tenías, la dejamos para imágenes de productos más adelante)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // --- Dependencias estándar de Compose y Android (no cambian) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("androidx.compose.foundation:foundation")

    // --- ¡ESTA ES LA LÍNEA QUE ARREGLA EL ERROR! ---
    // Contiene los íconos de GridView, ViewList, etc.
    implementation("androidx.compose.material:material-icons-extended-android:1.6.8")


    // --- Dependencias de Test (no cambian) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}