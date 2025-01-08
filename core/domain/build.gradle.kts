plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
    id("githubrepo.android.mockito")
}

android {
    namespace = "com.prac.domain"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {

    api(project(":core:data"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.retrofit)
}