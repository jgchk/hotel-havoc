package com.jgchk.hotelhavoc.features.game.items

import com.jgchk.hotelhavoc.R

object CookedPatty : Ingredient {
    override fun name() = "cooked patty"
    override fun drawable() = R.drawable.ic_cooked_patty
    override fun order() = 1
    override fun choppable() = false
    override fun cookable() = false
    override fun prepare() = CookedPatty
}