import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    id("githubrepo.android.application")
    id("githubrepo.android.application.compose")
    id("githubrepo.android.hilt")
    id("githubrepo.android.coroutines")
    id("githubrepo.android.mockito")
    id("githubrepo.android.serialization")
}

android {
    namespace = "com.prac.githubrepo"

    defaultConfig {
        applicationId = "com.prac.githubrepo"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false

            buildConfigField("String", "GITHUB_OAUTH_URI", "" + localProperties["GITHUB_OAUTH_URI"] + "")
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            buildConfigField("String", "GITHUB_OAUTH_URI", "" + localProperties["GITHUB_OAUTH_URI"] + "")
        }
    }
    sourceSets {
        getByName("debug") {
            java.srcDirs("src/debug/java")
        }
    }
}

dependencies {

    implementation(project(":data"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.androidx.navigation.testing)
    implementation(libs.androidx.junit.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.hamcrest)

    kaptTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.hilt.android.compiler)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.androidx.paging.compose)
    implementation(libs.compose.glide)
}