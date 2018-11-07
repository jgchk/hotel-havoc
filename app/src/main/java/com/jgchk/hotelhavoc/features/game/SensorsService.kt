package com.jgchk.hotelhavoc.features.game

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorsService
@Inject constructor(retrofit: Retrofit) : SensorsApi {
    private val sensorsApi by lazy { retrofit.create(SensorsApi::class.java) }

    override fun chop() = sensorsApi.chop()
    override fun cook() = sensorsApi.cook()
}