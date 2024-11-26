@file:Suppress("unused")

import com.android.build.api.dsl.ApplicationExtension
import com.prac.build.convention.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal class AndroidApplicationComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            extensions.configure<ApplicationExtension> {
                buildFeatures.compose = true
            }

            extensions.configure<ApplicationExtension> {
                configureAndroidCompose(this)
            }
        }
    }
}