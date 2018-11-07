package com.jgchk.hotelhavoc.features.game.items

import com.jgchk.hotelhavoc.R

object BottomBun : Ingredient {
    override fun name() = "bottom bun"
    override fun drawable() = R.drawable.ic_bun_bottom
    override fun order() = 0
    override fun choppable() = false
    override fun cookable() = false
    override fun prepare() = BottomBun
}