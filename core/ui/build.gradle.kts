plugins {
    alias(libs.plugins.atheer.android.library)
    alias(libs.plugins.atheer.android.compose)
}

dependencies {
    api(project(":core:designsystem"))
    implementation(project(":core:model"))

    // Exposed so every feature module can render remote images.
    api(libs.coil.compose)
    api(libs.coil.network.okhttp)

    testImplementation(libs.junit)
}
