plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}


android {
    namespace = "com.prac.core.common"
}

dependencies {

    implementation(project(":core:designsystem"))
    implementation(project(":core:exception"))
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