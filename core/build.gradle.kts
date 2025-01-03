plugins {
        alias(libs.plugins.android.library)
}

android {
        namespace = "silkways.tefmodloader.core"
        compileSdk = 35

        defaultConfig {
                minSdk = 24

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                consumerProguardFiles("consumer-rules.pro")
                externalNativeBuild {
                        cmake {
                                cppFlags("-std=c++23")
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
        externalNativeBuild {
                cmake {
                        path("src/main/cpp/CMakeLists.txt")
                        version = "3.22.1"
                }
        }
        compileOptions {
                sourceCompatibility = JavaVersion.VERSION_17
                targetCompatibility = JavaVersion.VERSION_17
        }
        ndkVersion = "28.0.12674087 rc2"
}

dependencies {

        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
}