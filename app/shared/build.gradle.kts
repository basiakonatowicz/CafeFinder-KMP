import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    jvm()
    
    /*js {
        browser()
    }
    */
    @OptIn(ExperimentalWasmDsl::class)
    /*wasmJs {
        browser()
    }
    */
    androidLibrary {
       namespace = "pl.konatowicz.cafefinder.app.shared"
       compileSdk = libs.versions.android.compileSdk.get().toInt()
       minSdk = libs.versions.android.minSdk.get().toInt()
    
       compilerOptions {
           jvmTarget = JvmTarget.JVM_11
       }
       androidResources {
           enable = true
       }
       withHostTest {
           isIncludeAndroidResources = true
       }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation("io.ktor:ktor-client-okhttp:3.4.1")
        }
        commonMain.dependencies {
            api(projects.core)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation("io.ktor:ktor-client-core:3.4.1")
            implementation("io.ktor:ktor-client-content-negotiation:3.4.1")
            implementation("io.ktor:ktor-serialization-kotlinx-json:3.4.1")
            // Biblioteka Kamel do asynchronicznego ładowania obrazków (wymóg prowadzącego)
            implementation("media.kamel:kamel-image-default:1.0.9")
            // Obsługa ViewModelu w Kotlin Multiplatform
            implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        /*jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }*/
        jvmMain.dependencies {
            // Silnik OkHttp dla Desktopu
            implementation("io.ktor:ktor-client-okhttp:3.4.1")
            // Brakująca biblioteka z wykładu! Odblokowuje wątki graficzne na PC
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.0")
            // Brakujący silnik dla biblioteki Kamel na Desktopie!
            implementation("io.ktor:ktor-client-cio:3.4.1")
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}