package com.jgchk.hotelhavoc.model.domain.ingredients

import com.jgchk.hotelhavoc.R
import com.jgchk.hotelhavoc.model.domain.Ingredient

class Tomato(name: String = "tomato") : Ingredient(name) {

    var chopped: Boolean = false

    override val image: Int
        get() = R.drawable.ic_tomato

    fun chop() {
        chopped = true
    }
}