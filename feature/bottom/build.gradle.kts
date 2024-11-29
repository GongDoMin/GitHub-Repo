plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.library.compose")
}

android {
    namespace = "com.prac.feature.bottom"
}

dependencies {

    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    
}