package io.radev.catchit

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.radev.catchit.data.DataRepository
import io.radev.catchit.data.DataRepositoryImpl
import io.radev.catchit.db.CatchItDatabase
import io.radev.catchit.db.DatabaseConstants
import io.radev.catchit.network.ApiConstants
import io.radev.catchit.network.ApiService
import io.radev.catchit.network.errorhandling.NetworkResponseAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/*
 * Created by radoslaw on 14/06/2020.
 * radev.io 2020.
 */



@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor
    }

    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(httpLoggingInterceptor)
            .build()

    @Provides
    fun provideApiService(okHttpClient: OkHttpClient): ApiService = Retrofit.Builder()
        .baseUrl(ApiConstants.API_BASE_URL)
        .addCallAdapterFactory(NetworkResponseAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(ApiService::class.java)
}

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    @Provides
    fun provideCatchItDatabase(@ApplicationContext context: Context): CatchItDatabase =
        Room.databaseBuilder(
            context,
            CatchItDatabase::class.java, DatabaseConstants.DATABASE_NAME
        ).build()
}

@Module
@InstallIn(ApplicationComponent::class)
abstract class DataRepositoryModule {

    @Binds
    abstract fun bindDataRepository(
        dataRepositoryImpl: DataRepositoryImpl
    ): DataRepository
}