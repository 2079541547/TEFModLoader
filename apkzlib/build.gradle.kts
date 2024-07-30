plugins {
    id("java-library")
}

java {
    sourceCompatibility = JavaVersion.VERSION_20
    targetCompatibility = JavaVersion.VERSION_20
}

dependencies {
    implementation(libs.jsr305)
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    api(libs.guava)
    api("com.android.tools.build:apksig:8.0.2")
    compileOnlyApi("com.google.auto.value:auto-value-annotations:1.10.1")
    annotationProcessor("com.google.auto.value:auto-value:1.10.1")
}
