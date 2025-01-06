plugins {
    id("githubrepo.android.library")
}

android {
    namespace = "com.prac.auth"
}

dependencies {
    implementation(project(":core:local"))
    testImplementation(project(":shared-test"))
}