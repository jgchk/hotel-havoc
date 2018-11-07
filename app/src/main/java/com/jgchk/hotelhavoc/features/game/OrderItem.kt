package com.jgchk.hotelhavoc.features.game

import com.jgchk.hotelhavoc.features.game.items.Ingredient

abstract class OrderItem {
    abstract val requiredIngredients: List<Ingredient>
    abstract var currentIngredients: Set<Ingredient>

    abstract fun isCombinableWith(item: OrderItem): Boolean
    abstract fun combineWith(item: OrderItem): OrderItem
    abstract fun drawables(): IntArray
    fun isChoppable() = currentIngredients.size == 1 && currentIngredients.first().choppable()
    fun isCookable() = currentIngredients.size == 1 && currentIngredients.first().cookable()
}