package com.atheer.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                namespace = "com.atheer"
                compileSdk { version = release(36) { minorApiLevel = 1 } }

                defaultConfig {
                    applicationId = "com.aistudio.atheervideo.qfxyr"
                    minSdk = 24
                    targetSdk = 36
                    versionCode = 1
                    versionName = "2.0.0"
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
                        all { test -> test.failOnNoDiscoveredTests.set(false) }
                    }
                }
            }
        }
    }
}
