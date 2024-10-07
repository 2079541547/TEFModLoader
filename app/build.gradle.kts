
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "silkways.terraria.efmodloader"
    compileSdk = 35

    defaultConfig {
        applicationId = "silkways.terraria.efmodloader"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_22
        targetCompatibility = JavaVersion.VERSION_22
    }
    kotlinOptions {
        jvmTarget = "22"
    }
    buildFeatures {
        viewBinding = true
        prefab = true
    }
    packagingOptions {
        pickFirst("**/libshadowhook.so")
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
    implementation(libs.shadowhook)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.core.ktx.v1130)
    implementation(libs.androidx.activity)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(libs.androidx.lifecycle.service)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
