plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}

android {
    namespace = "com.prac.feature.bottom"

    defaultConfig {
        testInstrumentationRunner = "com.prac.shared_test.CustomTestRunner"
    }
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    
}