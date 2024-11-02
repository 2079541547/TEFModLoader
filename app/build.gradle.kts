import com.android.build.gradle.internal.dsl.SigningConfig
import org.gradle.api.NamedDomainObjectContainer

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("../TEFModLoader.jks")
            keyAlias = "TEFModLoader"
            storePassword = "TEFModLoader"
            keyPassword = "EternalFuture"
        }
    }
    namespace = "silkways.terraria.efmodloader"
    compileSdk = 35

//    lint {
//        baseline = file("lint-baseline.xml")
//    }


    defaultConfig {
        applicationId = "silkways.terraria.efmodloader"
        minSdk = 24
        targetSdk = 35
        versionCode = 121
        versionName = "1.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildTypes {
        release {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
//            signingConfig = signingConfigs.getByName("debug")
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
        viewBinding = true
        prefab = true
    }

    ndkVersion = "28.0.12433566 rc1"

    aaptOptions {
        noCompress.add("assets/bin/Data/data.unity3d")
        noCompress.add("assets/bin/Data/resources.resource")
        noCompress.add("assets/bin/Data/unity default resources")
    }
    buildToolsVersion = "35.0.0"
}


dependencies{
    implementation(project(":core"))
    implementation(project(":game-assets"))
    implementation(libs.commonmark)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(libs.androidx.preference.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
