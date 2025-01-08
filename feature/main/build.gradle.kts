plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
    id("githubrepo.android.mockito")
}

android {
    namespace = "com.prac.feature.main"

    defaultConfig {
        testInstrumentationRunner = "com.prac.shared_test.CustomTestRunner"
    }
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.androidx.ui.test.junit4.android)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.androidx.paging.compose)
}