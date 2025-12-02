import org.gradle.internal.impldep.com.fasterxml.jackson.core.JsonPointer.compile

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.suitcaseapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.suitcaseapp"
        minSdk = 26
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}


dependencies {

    //implementation platform ("com.google.firebase:firebase-bom:32.6.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")
    implementation("com.google.firebase:firebase-auth:24.0.1")
    implementation ("com.firebaseui:firebase-ui-auth:9.1.1")
    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation ("com.google.firebase:firebase-storage:22.0.1")
    implementation ("com.squareup.picasso:picasso:2.71828")
    //implementation ("com.google.firebase:firebase-database:20.3.0")

    implementation ("com.github.bumptech.glide:glide:5.0.5")
    // Glide v4 uses this new annotation processor -- see https://bumptech.github.io/glide/doc/generatedapi.html
    annotationProcessor ("com.github.bumptech.glide:compiler:5.0.5")





    implementation ("com.firebaseui:firebase-ui-firestore:9.1.1")
    implementation ("com.google.firebase:firebase-analytics")


    implementation ("com.google.firebase:firebase-firestore:26.0.2")
    implementation("com.google.firebase:firebase-database:22.0.1")
    implementation("androidx.core:core-ktx:1.17.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}