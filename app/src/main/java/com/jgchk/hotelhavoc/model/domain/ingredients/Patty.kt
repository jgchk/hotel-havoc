package com.jgchk.hotelhavoc.model.domain.ingredients

import com.jgchk.hotelhavoc.R
import com.jgchk.hotelhavoc.model.domain.Ingredient

class Patty(name: String = "raw patty") : Ingredient(name) {

    var cookTime: Int = 0
    val cooked: Boolean
        get() = cookTime >= REQUIRED_COOK_TIME

    override val image: Int
        get() = when (cooked) {
            true -> R.drawable.ic_cooked_burger
            false -> R.drawable.ic_uncooked_burger
        }

    fun cook(time: Int) {
        cookTime += time
    }

    companion object {
        private const val REQUIRED_COOK_TIME = 1000
    }
}