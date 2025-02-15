import org.gradle.internal.configuration.problems.PropertyTrace

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("TEFModLoader.keystore")
            keyAlias = "TEFModLoader"
            storePassword = "EternalFuture"
            keyPassword = "TEFModLoader"
        }
    }
    namespace = "silkways.terraria.efmodloader"
    compileSdk = 35

    defaultConfig {
        applicationId = "silkways.terraria.efmodloader"
        minSdk = 24
        targetSdk = 35
        versionCode = 300
        versionName = "3.0.0 Stable"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
        ndkVersion = "28.0.12674087 rc2"
}

dependencies {
    compileOnly(project(":load"))

    implementation(libs.commonmark)

    implementation(libs.androidx.palette)

    implementation(libs.androidx.appcompat)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.com.google.accompanist.navigation.animation) //tab栏
    implementation(libs.androidx.compose.material.icons.extended) //图标
    implementation(libs.compose.destinations.animations.core)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.io.coil.kt.coil.compose)
    implementation(libs.me.zhanghai.android.appiconloader.coil)

    ksp(libs.compose.destinations.ksp)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}