plugins {
    kotlin("multiplatform")
    id("com.android.application")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization")
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                
                // Hilt
                implementation("com.google.dagger:hilt-android:2.51")
                implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
                
                // Ktor Client
                implementation("io.ktor:ktor-client-android:3.0.2")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.2")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.2")
                implementation("io.ktor:ktor-client-logging:3.0.2")
                implementation("io.ktor:ktor-client-auth:3.0.2")
                



                
                // Room
                implementation("androidx.room:room-runtime:2.6.0")
                implementation("androidx.room:room-ktx:2.6.0")
                
                // Kotlinx Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
                
                // DataStore
                implementation("androidx.datastore:datastore-preferences:1.0.0")
                
                // Navigation
                implementation("androidx.navigation:navigation-compose:2.7.5")
                
                // Coil for image loading
                implementation("io.coil-kt:coil-compose:2.5.0")
                
                // WorkManager for background sync
                implementation("androidx.work:work-runtime-ktx:2.9.0")
                implementation("androidx.hilt:hilt-work:1.2.0")
                
                // Lifecycle
                implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
                implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
                
                // Accompanist (for system UI controller, permissions, etc.)
                implementation("com.google.accompanist:accompanist-permissions:0.32.0")

                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
            }
        }
    }
}

// KSP configuration for Hilt and Room
dependencies {
    add("kspAndroid", "com.google.dagger:hilt-android-compiler:2.51")
    add("kspAndroid", "com.google.dagger:dagger-compiler:2.51")
    add("kspAndroid", "androidx.room:room-compiler:2.6.0")
    add("kspAndroid", "androidx.hilt:hilt-compiler:1.2.0")
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.sales.app"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        applicationId = "com.sales.app"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode = 1
        versionName = "1.0.0"
        
        // Room schema export
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlin {
        jvmToolchain(17)
    }
}
