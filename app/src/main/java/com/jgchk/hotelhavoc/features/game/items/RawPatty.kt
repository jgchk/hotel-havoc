package com.jgchk.hotelhavoc.features.game.items

import com.jgchk.hotelhavoc.R

object RawPatty : Ingredient {
    override fun name() = "raw patty"
    override fun drawable() = R.drawable.ic_raw_patty
    override fun order() = 0
    override fun choppable() = false
    override fun cookable() = true
    override fun prepare() = CookedPatty
}