package com.jgchk.hotelhavoc.features.game.items

import com.jgchk.hotelhavoc.R

object Lettuce : Ingredient {
    override fun name() = "lettuce"
    override fun drawable() = R.drawable.ic_lettuce
    override fun order() = 2
    override fun choppable() = true
    override fun cookable() = false
    override fun prepare() = Lettuce
}