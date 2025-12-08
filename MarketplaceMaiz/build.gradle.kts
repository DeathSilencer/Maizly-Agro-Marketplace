// Este es el build.gradle.kts de la RAÍZ del proyecto
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    //PARA FIREBASE (Tu versión)
    id("com.google.gms.google-services") version "4.4.4" apply false
}