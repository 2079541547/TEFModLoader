plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_20
    targetCompatibility = JavaVersion.VERSION_20
}

dependencies {
    implementation(libs.jsr305)
    implementation(libs.bcpkix.jdk15on)
    implementation(libs.bcprov.jdk15on)
    api(libs.guava)
    api("com.android.tools.build:apksig:8.0.2")
    compileOnlyApi(libs.value.auto.value.annotations)
    annotationProcessor(libs.value.auto.value)
}
