package com.example.saferun

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration

@HiltAndroidApp
class SafeRunApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // osmdroid needs a user agent and a preferences store before the first map is shown.
        Configuration.getInstance().apply {
            userAgentValue = packageName
            load(this@SafeRunApp, getSharedPreferences("osmdroid", MODE_PRIVATE))
        }
    }
}
