// Top-level build file using Kotlin DSL

plugins {
    id("com.android.application") version "8.4.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
