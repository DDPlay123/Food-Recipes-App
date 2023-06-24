plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs.kotlin")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "mai.project.foody"
    compileSdk = 33

    defaultConfig {
        applicationId = "mai.project.foody"
        minSdk = 24
        targetSdk = 33
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
    buildFeatures {
        dataBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // CoordinatorLayout
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // Navigation Component
    val versionNavigation = "2.6.0"
    implementation("androidx.navigation:navigation-fragment-ktx:$versionNavigation")
    implementation("androidx.navigation:navigation-ui-ktx:$versionNavigation")

    // Room Components
    val versionRoom = "2.5.2"
    implementation("androidx.room:room-runtime:$versionRoom")
    kapt("androidx.room:room-compiler:$versionRoom")
    implementation("androidx.room:room-ktx:$versionRoom")
    androidTestImplementation("androidx.room:room-testing:$versionRoom")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Retorfit
    val versionRetrofit = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$versionRetrofit")
    implementation("com.squareup.retrofit2:converter-gson:$versionRetrofit")

    // Dagger - Hilt
    val versionHilt = "2.46.1"
    implementation("com.google.dagger:hilt-android:$versionHilt")
    kapt("com.google.dagger:hilt-android-compiler:$versionHilt")

    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Image Loading Library Coil
    implementation("io.coil-kt:coil:1.4.0")

    // Gson
    implementation("com.google.code.gson:gson:2.9.0")

    // Shimmer (https://github.com/omtodkar/ShimmerRecyclerView)
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.todkars:shimmer-recyclerview:0.4.1")

    // Jsoup
    implementation("org.jsoup:jsoup:1.14.3")
}