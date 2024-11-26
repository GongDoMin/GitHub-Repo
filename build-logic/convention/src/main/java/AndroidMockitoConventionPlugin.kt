@file:Suppress("unused")

import com.prac.build.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidMockitoConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies {
                add("implementation", libs.findLibrary("mockito-core").get())
                add("implementation", libs.findLibrary("mockito-kotlin").get())
            }
        }
    }
}