plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
}

android {
    namespace = "com.prac.domain"
}

dependencies {

    implementation(project(":core:data"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.retrofit)
}