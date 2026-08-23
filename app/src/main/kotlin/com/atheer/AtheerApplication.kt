package com.atheer

import android.app.Application
import com.atheer.core.data.DemoContentSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AtheerApplication : Application() {

    @Inject lateinit var demoContentSeeder: DemoContentSeeder

    override fun onCreate() {
        super.onCreate()
        demoContentSeeder.seedOnStartup()
    }
}
