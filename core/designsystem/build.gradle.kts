plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}

android {
    namespace = "com.prac.core.designsystem"
}

dependencies {

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)

    implementation(libs.compose.glide)
}