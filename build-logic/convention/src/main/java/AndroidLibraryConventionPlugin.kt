@file:Suppress("unused")

import com.android.build.gradle.LibraryExtension
import com.prac.build.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.kapt")
                apply("githubrepo.android.hilt")
                apply("githubrepo.android.coroutines")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                defaultConfig.testInstrumentationRunner = "com.prac.shared_test.CustomTestRunner"
                buildFeatures.buildConfig = true
            }

            extensions.configure<KotlinProjectExtension> {
                jvmToolchain(21)
            }
        }
    }
}