import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project(":android:core"))
            implementation(project(":android:axml"))
            implementation(fileTree(mapOf("dir" to "src/androidMain/libs", "include" to listOf("*.jar", "*.aar"))))
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.material3)
            implementation(libs.material.icons.extended)
            implementation(libs.tomlkt)
            implementation(libs.skiko)
            implementation(libs.skiko.awt)
            implementation(libs.kotlin.logging.jvm)
            implementation(libs.apkzlib)
            implementation(libs.json)
            implementation(fileTree(mapOf("dir" to "src/commonMain/libs", "include" to listOf("*.jar", "*.aar"))))
        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(fileTree(mapOf("dir" to "src/desktopMain/libs", "include" to listOf("*.jar", "*.aar"))))
        }
    }
}

android {
    namespace = "silkways.terraria.efmodloader"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "silkways.terraria.efmodloader"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            resources.srcDirs("src/commonMain/resources")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("TEFModLoader.keystore")
            keyAlias = "TEFModLoader"
            storePassword = "EternalFuture"
            keyPassword = "TEFModLoader"
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "silkways.terraria.efmodloader.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "silkways.terraria.efmodloader"
            packageVersion = "1.0.0"
        }
    }
}
