plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.medimap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medimap"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.play.services.maps)
    implementation(libs.play.services.ads)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}