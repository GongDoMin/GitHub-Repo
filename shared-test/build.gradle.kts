plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
}

android {
    namespace = "com.prac.shared_test"
}

dependencies {

    implementation(project(":app"))

    implementation(project(":feature:login"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:main"))

    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:exception"))
    implementation(project(":core:local"))
    implementation(project(":core:network"))

    implementation(libs.retrofit)
}