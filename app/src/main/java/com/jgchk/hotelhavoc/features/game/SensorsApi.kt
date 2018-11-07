package com.jgchk.hotelhavoc.features.game

import com.jgchk.hotelhavoc.features.game.readings.AccelerationReading
import retrofit2.Call
import retrofit2.http.GET

internal interface SensorsApi {
    @GET("chop") fun chop(): Call<AccelerationReading>
    @GET("cook") fun cook(): Call<AccelerationReading>
}