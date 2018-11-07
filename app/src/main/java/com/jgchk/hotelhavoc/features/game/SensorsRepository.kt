package com.jgchk.hotelhavoc.features.game

import com.jgchk.hotelhavoc.core.exception.Failure
import com.jgchk.hotelhavoc.core.functional.Either
import com.jgchk.hotelhavoc.core.platform.NetworkHandler
import com.jgchk.hotelhavoc.features.game.readings.AccelerationReading
import retrofit2.Call
import javax.inject.Inject

interface SensorsRepository {
    fun choppingReading(): Either<Failure, AccelerationReading>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: SensorsService) : SensorsRepository {
        override fun choppingReading(): Either<Failure, AccelerationReading> {
            return when (networkHandler.isConnected) {
                true -> request(service.chop())
                false, null -> Either.Left(Failure.NetworkConnection())
            }
        }

        private fun <T> request(call: Call<T>): Either<Failure, T> {
            val response = call.execute()
            return when (response.isSuccessful) {
                true -> Either.Right(response.body()!!)
                false -> Either.Left(Failure.ServerError())
            }
        }
    }
}