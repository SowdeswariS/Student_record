plugins {
    id("com.android.application")
    id("io.github.jan.supabase") version "2.2.0"
}

android {
    namespace = "com.example.studentrecord"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.studentrecord"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
    // Supabase
    implementation("io.github.jan.supabase:supabase-android:2.2.0")
    implementation("io.github.jan.supabase:postgrest-kt:2.2.0")
    implementation("io.github.jan.supabase:auth-kt:2.2.0")
    implementation("io.github.jan.supabase:storage-kt:2.2.0")
    implementation("io.github.jan.supabase:realtime-kt:2.2.0")

    // AndroidX + Material
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
