package com.jgchk.hotelhavoc.features.game.items

import com.jgchk.hotelhavoc.R

object ChoppedLettuce : Ingredient {
    override fun name() = "chopped lettuce"
    override fun drawable() = R.drawable.ic_chopped_lettuce
    override fun order() = 2
    override fun choppable() = false
    override fun cookable() = false
    override fun prepare() = ChoppedLettuce
}