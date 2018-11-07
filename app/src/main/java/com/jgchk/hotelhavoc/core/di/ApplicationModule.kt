/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jgchk.hotelhavoc.core.di

import android.content.Context
import com.jgchk.hotelhavoc.AndroidApplication
import com.jgchk.hotelhavoc.BuildConfig
import com.jgchk.hotelhavoc.features.game.SensorsRepository
import com.jgchk.hotelhavoc.features.menu.LoginRepository
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: AndroidApplication) {

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
                .baseUrl("https://graph.api.smartthings.com/api/smartapps/installations/a60c410e-6a8a-423c-9e22-73945d1bbbe8/")
                .client(createClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    private fun createClient(): OkHttpClient {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        okHttpClientBuilder.addInterceptor {
            val original = it.request()
            val request = original.newBuilder()
                    .header("Authorization", "Bearer 2f954bb9-ada0-49ae-822d-5630b8ea69d2")
                    .method(original.method(), original.body())
                    .build()
            it.proceed(request)
        }
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        }
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideLoginRepository(dataSource: LoginRepository.Network): LoginRepository = dataSource

    @Provides
    @Singleton
    fun provideSensorsRepository(dataSource: SensorsRepository.Network): SensorsRepository = dataSource
}
