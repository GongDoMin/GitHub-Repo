plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("AndroidApplicationPlugin") {
            id = "githubrepo.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }

        register("AndroidApplicationComposePlugin") {
            id = "githubrepo.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }

        register("AndroidLibraryPlugin") {
            id = "githubrepo.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }

        register("AndroidLibraryComposePlugin") {
            id = "githubrepo.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }

        register("AndroidHiltPlugin") {
            id = "githubrepo.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }

        register("AndroidCoroutinesPlugin") {
            id = "githubrepo.android.coroutines"
            implementationClass = "AndroidCoroutinesConventionPlugin"
        }

        register("AndroidRoomPlugin") {
            id = "githubrepo.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }

        register("AndroidSerializationPlugin") {
            id = "githubrepo.android.serialization"
            implementationClass = "AndroidSerializationConventionPlugin"
        }

        register("AndroidMockitoPlugin") {
            id = "githubrepo.android.mockito"
            implementationClass = "AndroidMockitoConventionPlugin"
        }
    }
}