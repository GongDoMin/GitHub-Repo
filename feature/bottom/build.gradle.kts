plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}

android {
    namespace = "com.prac.feature.bottom"
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    
}