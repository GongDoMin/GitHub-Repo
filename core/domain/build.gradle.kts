plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
}

android {
    namespace = "com.prac.domain"
}

dependencies {

    api(project(":core:data"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.retrofit)
}