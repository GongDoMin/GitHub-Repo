package com.prac.build.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            add("implementation", platform(libs.findLibrary("compose.bom").get()))
            add("androidTestImplementation", platform(libs.findLibrary("compose.bom").get()))
            add("implementation", libs.findBundle("compose").get())
            add("implementation", libs.findBundle("compose-debug").get())
        }
    }
}