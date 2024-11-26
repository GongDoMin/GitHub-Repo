import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.serialization")
    id("githubrepo.android.mockito")
}

android {
    namespace = "com.prac.network"

    buildTypes {
        debug {
            buildConfigField("String", "GITHUB_URL", "\"https://github.com/\"")
            buildConfigField("String", "GITHUB_API_URL", "\"https://api.github.com/\"")
            buildConfigField("String", "CLIENT_ID", "" + localProperties["CLIENT_ID"] + "")
            buildConfigField("String", "CLIENT_SECRET", "" + localProperties["CLIENT_SECRET"] + "")
        }

        release {
            buildConfigField("String", "GITHUB_URL", "\"https://github.com/\"")
            buildConfigField("String", "GITHUB_API_URL", "\"https://api.github.com/\"")
            buildConfigField("String", "CLIENT_ID", "" + localProperties["CLIENT_ID"] + "")
            buildConfigField("String", "CLIENT_SECRET", "" + localProperties["CLIENT_SECRET"] + "")
        }
    }
}

dependencies {
    implementation(project(":local"))
    testImplementation(project(":shared-test"))

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.serialization.converter)
}