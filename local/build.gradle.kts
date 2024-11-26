import com.google.protobuf.gradle.id

plugins {
    id("githubrepo.android.library")
    id("githubrepo.android.room")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "com.prac.local"

    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas"
                    )
                )
            }
        }
    }

    sourceSets {
        getByName("androidTest").assets.srcDirs(files("$projectDir/schemas"))
    }
}

dependencies {

    testImplementation(project(":shared-test"))

    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.junit)

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.protobuf)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
    }
}