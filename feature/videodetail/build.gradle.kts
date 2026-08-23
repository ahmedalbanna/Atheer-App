plugins {
    alias(libs.plugins.atheer.android.library)
    alias(libs.plugins.atheer.android.compose)
    alias(libs.plugins.atheer.hilt)
}

dependencies {
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))

    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Video playback (Media3 / ExoPlayer replaces the deprecated VideoView)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
}
