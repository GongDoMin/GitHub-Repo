plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
}

android {
    namespace = "com.prac.data"
}

dependencies {

    implementation(project(":core:network"))
    implementation(project(":core:local"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.retrofit)
}