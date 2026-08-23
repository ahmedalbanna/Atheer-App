package com.atheer.buildlogic

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                namespace = target.namespace
                compileSdk { version = release(36) { minorApiLevel = 1 } }

                defaultConfig {
                    minSdk = 24
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_17
                    targetCompatibility = JavaVersion.VERSION_17
                }

                buildFeatures {
                    buildConfig = false
                }

                testOptions {
                    unitTests {
                        isIncludeAndroidResources = true
                        // Modules without tests must not fail the build.
                        all { test -> test.failOnNoDiscoveredTests.set(false) }
                    }
                }
            }
        }
    }
}
