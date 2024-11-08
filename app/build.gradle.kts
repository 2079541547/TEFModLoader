import com.android.build.gradle.internal.dsl.SigningConfig
import org.gradle.api.NamedDomainObjectContainer

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("TEFModLoader")
            keyAlias = "TEFModLoader"
            storePassword = "EternalFuture"
            keyPassword = "TEFModLoader"
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
        versionCode = 150
        versionName = "1.5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildToolsVersion = "35.0.0"
}


dependencies{
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
