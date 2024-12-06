plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
    id("githubrepo.android.mockito")
}

android {
    namespace = "com.prac.feature.profile"
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.ui.test.junit4.android)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.androidx.paging.compose)
}