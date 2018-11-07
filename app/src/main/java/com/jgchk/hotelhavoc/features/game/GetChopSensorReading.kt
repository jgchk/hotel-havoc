package com.jgchk.hotelhavoc.features.game

import com.jgchk.hotelhavoc.core.interactor.UseCase
import com.jgchk.hotelhavoc.features.game.readings.AccelerationReading
import javax.inject.Inject

class GetChopSensorReading
@Inject constructor(private val sensorsRepository: SensorsRepository) : UseCase<AccelerationReading, UseCase.None>() {
    override suspend fun run(params: None) = sensorsRepository.choppingReading()
}