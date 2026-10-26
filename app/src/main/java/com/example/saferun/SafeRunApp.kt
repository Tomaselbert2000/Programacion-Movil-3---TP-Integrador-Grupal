package com.example.saferun

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import java.io.File

@HiltAndroidApp
class SafeRunApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // osmdroid needs a user agent, a preferences store and a writable cache
        // before the first map is shown. Keep everything inside the app sandbox.
        Configuration.getInstance().apply {
            userAgentValue = packageName
            load(this@SafeRunApp, getSharedPreferences("osmdroid", MODE_PRIVATE))
            osmdroidBasePath = File(cacheDir, "osmdroid")
            osmdroidTileCache = File(cacheDir, "osmdroid/tiles")
        }
    }
}
