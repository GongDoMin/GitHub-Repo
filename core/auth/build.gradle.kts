plugins {
    id("githubrepo.android.library")
}

android {
    namespace = "com.prac.auth"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":core:local"))
    testImplementation(project(":shared-test"))
}