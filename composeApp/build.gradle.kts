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
        
        //noinspection WrongGradleMethod
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(project(":android:core"))
            implementation(project(":android:axml"))
            implementation(project(":android:axml"))
            // implementation(project(":game-assets"))
            implementation(fileTree(mapOf("dir" to "src/androidMain/libs", "include" to listOf("*.jar", "*.aar"))))
        }
        //noinspection WrongGradleMethod
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
            implementation(libs.kermit)
            implementation(libs.apkzlib)
            implementation("com.android.tools.build:apksig:8.10.0-alpha05") {
                exclude("org.bouncycastle")
            }
            implementation(libs.bcprov.jdk18on)
            implementation(libs.bcpkix.jdk18on)
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
    namespace = "eternal.future.tefmodloader"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "eternal.future.tefmodloader"
        minSdkVersion(libs.versions.android.minSdk.get().toInt())
        //noinspection OldTargetApi
        targetSdkVersion(libs.versions.android.targetSdk.get().toInt())
        versionCode = 1000
        versionName = "10.0.0 Beta3.5"
        multiDexEnabled = true

        ndk {
            //noinspection ChromeOsAbiSupport
            // abiFilters += "arm64-v8a"
        }
    }

    aaptOptions {
       noCompress += mutableListOf(
            "assets/bin/Data/data.unity3d",
            "assets/bin/Data/resources.resource",
            "assets/bin/Data/unity default resources"
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dexOptions {
        keepRuntimeAnnotatedClasses = false
        jumboMode = true
        preDexLibraries = true
    }


    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            isZipAlignEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
            storeFile = file("TEFModLoader.p12")
            keyAlias = "EternalFuture"
            storePassword = "TEFModLoader"
            keyPassword = "TEFModLoader"
        }
    }

    packagingOptions {
        exclude("**/libTEFModLoader.so")
        exclude("**/libauxiliary.so")
        exclude("**/libTEFLoader.so")
        pickFirst("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
        exclude("**/libdobby.so")
        exclude("**/libexample1.so")
        exclude("**/libexample2.so")
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "eternal.future.tefmodloader.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.AppImage)
            packageName = "eternal.future.tefmodloader"
            packageVersion = "10.0.0"
        }
    }
}