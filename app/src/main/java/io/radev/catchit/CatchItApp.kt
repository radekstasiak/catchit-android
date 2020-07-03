package io.radev.catchit

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp

/*
 * Created by radek on 29/05/2020.
 * radev.io 2020.
 */
@HiltAndroidApp
class CatchItApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this);
    }
}