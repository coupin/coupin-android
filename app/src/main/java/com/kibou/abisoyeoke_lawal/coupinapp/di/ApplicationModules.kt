package com.kibou.abisoyeoke_lawal.coupinapp.di

import com.kibou.abisoyeoke_lawal.coupinapp.CoupinApp
import com.kibou.abisoyeoke_lawal.coupinapp.R
import com.kibou.abisoyeoke_lawal.coupinapp.utils.googleMapsApiBaseURL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModules {

    @GoogleMapsRetrofit
    @Provides
    fun getRetrofit() : Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
                .baseUrl(googleMapsApiBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }

    @CoupinRetrofit
    @Provides
    fun getCoupinRetrofit() : Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()

        return Retrofit.Builder()
                .baseUrl(CoupinApp.getContext().getString(R.string.base_url) + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
    }
}