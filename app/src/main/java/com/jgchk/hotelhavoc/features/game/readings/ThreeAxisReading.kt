package com.jgchk.hotelhavoc.features.game.readings

data class ThreeAxisReading(val x: Int, val y: Int, val z: Int) {
    override fun toString() = "(x=$x, y=$y, z=$z)"
}