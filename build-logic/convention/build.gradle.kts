plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "atheer.android.application"
            implementationClass = "com.atheer.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "atheer.android.library"
            implementationClass = "com.atheer.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "atheer.android.compose"
            implementationClass = "com.atheer.buildlogic.AndroidComposeConventionPlugin"
        }
        register("androidRoom") {
            id = "atheer.android.room"
            implementationClass = "com.atheer.buildlogic.AndroidRoomConventionPlugin"
        }
        register("hilt") {
            id = "atheer.hilt"
            implementationClass = "com.atheer.buildlogic.HiltConventionPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}
