plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
}

android {
    namespace = "com.prac.shared_test"

    sourceSets {
        getByName("debug") {
            java.srcDirs("src/debug/java")
        }
    }
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:local"))
    implementation(project(":core:network"))

    implementation(project(":feature:login"))
    implementation(project(":feature:main"))

    implementation(libs.androidx.runner)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.retrofit)
}