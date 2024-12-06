import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}

android {
    namespace = "com.prac.feature.login"

    buildTypes {
        debug {
            buildConfigField("String", "GITHUB_OAUTH_URI", "" + localProperties["GITHUB_OAUTH_URI"] + "")
        }

        release {
            buildConfigField("String", "GITHUB_OAUTH_URI", "" + localProperties["GITHUB_OAUTH_URI"] + "")
        }
    }
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:exception"))
    implementation(project(":core:navigation"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.ui.test.junit4.android)
    testImplementation(libs.junit)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
}