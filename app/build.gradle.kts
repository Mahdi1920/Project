plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "tn.esprit.project"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "tn.esprit.project"
        minSdk = 29
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation("androidx.room:room-runtime:2.8.2")
    annotationProcessor("androidx.room:room-compiler:2.8.2")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment:2.7.2")
    implementation("androidx.navigation:navigation-ui:2.7.2")

    // Gson for JSON conversion in Room TypeConverters
    implementation("com.google.code.gson:gson:2.10.1")

    // Glide for image loading (avatars)
//    implementation("com.github.bumptech.glide:glide:4.15.1")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")


    testImplementation(libs.junit)
    // Core testing for LiveData (InstantTaskExecutorRule)
    testImplementation("androidx.arch.core:core-testing:2.2.0")

    // Mockito optional for mocking (not required, using manual fakes in tests)
    // testImplementation("org.mockito:mockito-core:4.8.1")

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}