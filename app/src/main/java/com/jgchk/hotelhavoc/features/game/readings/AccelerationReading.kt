package com.jgchk.hotelhavoc.features.game.readings

import com.google.gson.annotations.SerializedName

data class AccelerationReading(
        @SerializedName("acceleration") val isAccelerating: String,
        @SerializedName("threeAxis") val threeAxisReading: ThreeAxisReading) {
    override fun toString() = "AccelerationReading(isAccelerating=$isAccelerating, threeAxisReading=$threeAxisReading)"
}