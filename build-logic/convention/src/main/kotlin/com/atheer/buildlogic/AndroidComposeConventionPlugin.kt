package com.atheer.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            val composeBom = libs.findLibrary("androidx-compose-bom").get()
            listOf(
                libs.findLibrary("androidx-compose-foundation").get(),
                libs.findLibrary("androidx-compose-material-icons-extended").get(),
                libs.findLibrary("androidx-compose-material3").get(),
                libs.findLibrary("androidx-compose-ui").get(),
                libs.findLibrary("androidx-compose-ui-graphics").get(),
                libs.findLibrary("androidx-compose-ui-tooling-preview").get(),
                libs.findLibrary("androidx-lifecycle-runtime-compose").get(),
            ).forEach { dependency ->
                addImplementation(dependency)
            }

            runCatching {
                extensions.getByType<ApplicationExtension>().apply {
                    buildFeatures.compose = true
                }
            }.recoverCatching {
                extensions.getByType<LibraryExtension>().apply {
                    buildFeatures.compose = true
                }
            }

            dependencies {
                add("implementation", platform(composeBom))
                add("debugImplementation", libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
    }
}

internal fun Project.addImplementation(provider: Any) {
    dependencies.add("implementation", provider)
}
