@file:Suppress("unused")

import com.prac.build.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.google.dagger.hilt.android")
            }

            dependencies {
                add("implementation", libs.findLibrary("dagger-hilt-android").get())
                add("implementation", libs.findLibrary("hilt-android-testing").get())
                add("kapt", libs.findLibrary("dagger-hilt-compiler").get())
            }
        }
    }
}