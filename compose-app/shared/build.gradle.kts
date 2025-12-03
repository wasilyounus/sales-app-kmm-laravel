plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose") 
    id("com.google.devtools.ksp") version "2.0.21-1.0.25"
    id("androidx.room") version "2.7.0-alpha01"
    kotlin("plugin.serialization") version "2.0.21"
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidTarget()
    jvm("desktop")
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.material3)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation(compose.materialIconsExtended)
                
                // KMP Navigation & Lifecycle
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.7.0-alpha07")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.0")

                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

                // DateTime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

                // Ktor
                implementation("io.ktor:ktor-client-core:3.0.0")
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0")
                implementation("io.ktor:ktor-client-logging:3.0.2")   // ← missing!

                // Room
                implementation("androidx.room:room-runtime:2.7.0-alpha01")
                implementation("androidx.sqlite:sqlite-bundled:2.5.0-alpha01")
                
                // DataStore
                implementation("androidx.datastore:datastore-preferences-core:1.1.0")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
                implementation("io.mockk:mockk:1.13.8")
            }
        }
        
        val androidMain by getting {
            dependencies {
                api("androidx.activity:activity-compose:1.9.0")
                api("androidx.appcompat:appcompat:1.7.0")
                api("androidx.core:core-ktx:1.13.1")
                implementation("io.ktor:ktor-client-okhttp:3.0.0")
            }
        }
    
        val iosMain by creating {
            dependsOn(commonMain)
            val iosX64Main by getting
            val iosArm64Main by getting
            val iosSimulatorArm64Main by getting
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.0.0")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation("io.ktor:ktor-client-cio:3.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
            }
        }
    }
    
    dependencies {
        add("kspAndroid", "androidx.room:room-compiler:2.7.0-alpha01")
        add("kspDesktop", "androidx.room:room-compiler:2.7.0-alpha01")
        add("kspIosX64", "androidx.room:room-compiler:2.7.0-alpha01")
        add("kspIosArm64", "androidx.room:room-compiler:2.7.0-alpha01")
        add("kspIosSimulatorArm64", "androidx.room:room-compiler:2.7.0-alpha01")
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "in.wyco.salesapp.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}
