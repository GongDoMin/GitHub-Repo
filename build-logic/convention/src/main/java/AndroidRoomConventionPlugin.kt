@file:Suppress("unused")

import com.prac.build.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("androidx-room-runtime").get())
                add("implementation", libs.findLibrary("androidx-room-ktx").get())
                add("implementation", libs.findLibrary("androidx-room-paging").get())
                add("kapt", libs.findLibrary("androidx-room-compiler").get())
                add("androidTestImplementation", libs.findLibrary("androidx-room-testing").get())
            }
        }
    }
}