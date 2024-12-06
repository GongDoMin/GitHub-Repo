plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}

android {
    namespace = "com.prac.feature.home"
}

dependencies {

    implementation(project(":feature:main"))
    implementation(project(":feature:detail"))

    implementation(project(":core:navigation"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}