package com.jgchk.hotelhavoc.model.domain.ingredients

import com.jgchk.hotelhavoc.R
import com.jgchk.hotelhavoc.model.domain.Ingredient

class Lettuce(name: String = "lettuce") : Ingredient(name) {

    var chopped: Boolean = false
        private set

    override val image: Int
        get() = R.drawable.ic_lettuce

    fun chop() {
        chopped = true
    }
}