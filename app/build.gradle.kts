plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.medimap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.medimap"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
//
//    // Retrofit dependencies (already present, ensure only one version)
//    implementation("com.squareup.retrofit2:retrofit:2.9.0")
//    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
//
//    // AndroidX and Material Components
//    implementation("androidx.appcompat:appcompat:1.1.0")
//    implementation("com.google.android.material:material:1.10.0")
//
//    // MPAndroidChart
//    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
//
//    // OkHttp for networking and logging (ensure only one logging-interceptor)
//    implementation("com.squareup.okhttp3:okhttp:4.9.3")
//    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
//
//    // OSMdroid for map handling
//    implementation("org.osmdroid:osmdroid-android:6.1.11")
//
//    // Glide for image loading
//    implementation("com.github.bumptech.glide:glide:4.15.1")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
//
//    // Play Services Maps
//    implementation(libs.play.services.maps)
//
//    // Lifecycle components
//    implementation(libs.lifecycle.livedata.ktx)
//    implementation(libs.lifecycle.viewmodel.ktx)
//
//    // Navigation components
//    implementation(libs.navigation.fragment)
//    implementation(libs.navigation.ui)
//
//    // ConstraintLayout
//    implementation(libs.constraintlayout)
//
//    // Test dependencies
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
    ////////////////////////////////////////////////////
    implementation ("com.google.android.material:material:1.6.0")

    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.23")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("androidx.appcompat:appcompat:1.1.0")
    implementation ("com.google.android.material:material:1.0.0")

    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation ("org.osmdroid:osmdroid-android:6.1.11")
    implementation("androidx.room:room-runtime:2.5.2")
    implementation(libs.room.common)
    annotationProcessor("androidx.room:room-compiler:2.5.2")

    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.play.services.maps)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation(libs.constraintlayout)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}