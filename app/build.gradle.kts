import kotlinx.coroutines.flow.firstOrNull
import java.io.FileInputStream
import java.util.Properties

val localProperties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "com.prac.githubrepo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.prac.githubrepo"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.prac.githubrepo.CustomTestRunner"
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
        }
    }
    sourceSets {
        getByName("debug") {
            java.srcDirs("src/debug/java")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.ui.test.junit4.android)
    implementation(libs.androidx.navigation.testing)
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.turbine)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.espresso.intents)
    androidTestImplementation(libs.androidx.espresso.contrib)
    androidTestImplementation(libs.hamcrest)

    implementation(project(":data"))
    testImplementation(project(":shared-test"))
    androidTestImplementation(project(":shared-test"))

    implementation(libs.jetbrains.kotlinx.coroutines)
    implementation(libs.jetbrains.kotlinx.coroutines.test)

    implementation(libs.dagger.hilt.android)
    androidTestImplementation(libs.hilt.android.testing)
    testImplementation(libs.hilt.android.testing)
    kaptTest(libs.hilt.android.compiler)
    kaptAndroidTest(libs.hilt.android.compiler)
    kapt(libs.dagger.hilt.compiler)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)

    implementation(libs.androidx.paging)

    implementation(platform(libs.compose.bom))
    androidTestImplementation(platform(libs.compose.bom))

    implementation(libs.androidx.runtime)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.foundation.layout)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.paging.compose)
    implementation(libs.compose.glide)

    implementation(libs.jetbrains.kotlinx.serialization)
}