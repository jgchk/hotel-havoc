package com.jgchk.hotelhavoc.features.game

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log
import javax.inject.Inject
import kotlin.math.min

class SensorProcessor
@Inject constructor() {

    companion object {
        private val TAG = SensorProcessor::class.qualifiedName
    }

    private var lastX: Float = 0F
    private var lastY: Float = 0F
    private var lastZ: Float = 0F
    private var lastUpdate: Long = 0

    private var speedHistory = mutableListOf<Int>()

    fun isShakeGesture(sensorEvent: SensorEvent): Boolean {
        val sensor = sensorEvent.sensor

        if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = sensorEvent.values[0]
            val y = sensorEvent.values[1]
            val z = sensorEvent.values[2]

            val time = System.currentTimeMillis()

            val diffTime = time - lastUpdate
            if (diffTime > 100) {
                lastUpdate = time
                takeSpeedMeasurement(x, y, z, diffTime)
                return isGesture()
            }

            lastX = x
            lastY = y
            lastZ = z
        }
        return false
    }

    fun takeSpeedMeasurement(x: Float, y: Float, z: Float, diffTime: Long) {
        val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000
        speedHistory.add(0, speed.toInt())
    }

    fun isGesture(): Boolean {
        val avg = speedHistory.subList(0, min(10, speedHistory.size)).average()
        Log.d(TAG, avg.toString())
        return avg > 300
    }
}