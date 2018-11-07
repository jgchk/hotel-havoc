package com.jgchk.hotelhavoc.features.game.items

import com.jgchk.hotelhavoc.R

object TopBun : Ingredient {
    override fun name() = "top bun"
    override fun drawable() = R.drawable.ic_bun_top
    override fun order() = 3
    override fun choppable() = false
    override fun cookable() = false
    override fun prepare() = TopBun
}