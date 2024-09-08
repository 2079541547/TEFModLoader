
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "silkways.terraria.toolbox"
    compileSdk = 35

    defaultConfig {
        applicationId = "silkways.terraria.toolbox"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        viewBinding = true
        prefab = true
    }
    packagingOptions {
        pickFirst("**/libshadowhook.so")
    }

    ndkVersion = "27.1.12297006"

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
    implementation(project(":apkzlib"))
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
    implementation(files("libs/classes.jar"))
    implementation(libs.androidx.lifecycle.service)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
