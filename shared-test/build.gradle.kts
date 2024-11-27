plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
}

android {
    namespace = "com.prac.shared_test"
}

dependencies {

    implementation(project(":app"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":local"))
    implementation(project(":network"))

    implementation(libs.retrofit)
}