import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
}

android {
    namespace = "com.prac.data"

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

    implementation(project(":core:local"))
    implementation(project(":network"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.retrofit)
}