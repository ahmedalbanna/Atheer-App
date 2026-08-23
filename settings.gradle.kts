pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    // Convention plugins shared with every module.
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Atheer"

include(":app")
include(":core:model")
include(":core:common")
include(":core:database")
include(":core:data")
include(":core:designsystem")
include(":core:ui")
include(":feature:home")
include(":feature:shorts")
include(":feature:create")
include(":feature:subscriptions")
include(":feature:search")
include(":feature:library")
include(":feature:videodetail")
