plugins {
    alias(libs.plugins.atheer.android.library)
    alias(libs.plugins.atheer.android.room)
    alias(libs.plugins.atheer.hilt)
}

dependencies {
    implementation(project(":core:model"))
}
