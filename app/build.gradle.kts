plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.eventorias"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.eventorias"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation("androidx.navigation:navigation-compose:2.8.6")

        //Material3 Dependencies for more Icons
    implementation("androidx.compose.material3:material3:1.4.0-alpha05")
    implementation("androidx.compose.material:material-icons-extended:1.6.0-alpha05")

    // Coil (Image loading)
    implementation(libs.coil.compose)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication KTX for coroutines
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-appcheck-debug")

    // Firebase notification dependency
    implementation(libs.firebase.messaging.ktx)
    implementation("com.google.accompanist:accompanist-permissions:0.37.0")

// FireStore and Firebase-Storage dependencies
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-appcheck-debug")


    // Coroutine support for Firebase tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.5.2")

    // FirebaseUI
    implementation("com.firebaseui:firebase-ui-auth:8.0.1")

    //Google Play Services
    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("com.google.android.gms:play-services-base:18.5.0")

    //Koin Dependencies
    implementation("io.insert-koin:koin-android:4.0.0") // for Android
    implementation("io.insert-koin:koin-androidx-compose:4.0.0") // for Jetpack Compose


    //okhttp Dependency
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

// Dependency for modern date and time API from Java 8 to older Android versions
    //implementation("com.jakewharton.threetenabp:threetenabp:1.4.8")
    implementation ("org.threeten:threetenbp:1.5.1")
    //implementation ("org.threeten:threetenbp:1.5.1:no-tzdb") // Optional if time-zone database is not needed

    // Unit testing
    testImplementation("junit:junit:4.13.2")
    //testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    //testImplementation("org.mockito:mockito-core:4.11.0")
    testImplementation ("org.mockito:mockito-core:4.5.1")

    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-inline:4.11.0")
    testImplementation ("com.google.firebase:firebase-firestore:24.0.0 ")

    testImplementation ("androidx.arch.core:core-testing:2.1.0")
    testImplementation("org.mockito:mockito-inline:4.0.0")

    testImplementation("io.insert-koin:koin-test:3.2.0")
// Firebase dependencies for testing
    testImplementation ("com.google.firebase:firebase-auth:21.0.7")
    testImplementation ("io.mockk:mockk:1.12.0")

    testImplementation("com.jakewharton.threetenabp:threetenabp:1.4.8")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // JUnit 5 requires this for the platform launcher
    testImplementation("org.junit.platform:junit-platform-launcher:1.9.1")
}

/*// Enable JUnit Platform for JUnit 5
tasks.withType<Test> {
    useJUnitPlatform()
}*/