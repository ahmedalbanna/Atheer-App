package com.atheer.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal const val NAMESPACE_PREFIX = "com.atheer"

internal val Project.namespace: String
    get() = NAMESPACE_PREFIX + path.replace(':', '.')
