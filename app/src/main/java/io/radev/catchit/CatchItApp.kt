package io.radev.catchit

import android.app.Application
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/*
 * Created by radek on 29/05/2020.
 * radev.io 2020.
 */

class CatchItApp: Application(){

    companion object{
        lateinit var apiService: ApiService
    }

    override fun onCreate() {
        super.onCreate()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(ApiConstants.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}