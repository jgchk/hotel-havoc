package com.jgchk.hotelhavoc.model.domain

abstract class Ingredient(override val name: String) : Item {

    val prepared = false

}