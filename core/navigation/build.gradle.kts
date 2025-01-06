plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
    id("githubrepo.android.serialization")
}


android {
    namespace = "com.prac.core.navigation"
}

dependencies {

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
}