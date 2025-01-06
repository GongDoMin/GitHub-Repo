plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}


android {
    namespace = "com.prac.core.common"
}

dependencies {

    api(project(":core:designsystem"))
    api(project(":core:domain"))
    testImplementation(project(":shared-test"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.compose.glide)
}