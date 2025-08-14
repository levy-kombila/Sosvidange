package com.ap3.sosvidange

import android.app.Application
import com.cloudinary.android.MediaManager

class CloudinaryApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        val config = HashMap<String, String>()
        config["cloud_name"] = "daczmgm5e"
        config["api_key"] = "837286526413241"
        config["api_secret"] = "3M_jLYIDdIu5tsMpitJGy--qEjE"

        MediaManager.init(this, config)
    }
}