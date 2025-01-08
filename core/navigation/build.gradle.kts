plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
    id("githubrepo.android.serialization")
}


android {
    namespace = "com.prac.core.navigation"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}