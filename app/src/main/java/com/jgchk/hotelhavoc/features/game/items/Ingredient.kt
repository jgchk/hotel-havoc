package com.jgchk.hotelhavoc.features.game.items

interface Ingredient {
    fun name(): String
    fun drawable(): Int
    fun order(): Int
    fun choppable(): Boolean
    fun cookable(): Boolean
    fun prepare(): Ingredient
}