package io.radev.catchit

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import io.radev.catchit.network.ApiConstants
import io.radev.catchit.network.ApiService
import io.radev.catchit.updateTimetableAlarm.UpdateTimetableAlarmManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

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