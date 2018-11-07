package com.jgchk.hotelhavoc.features.game.items

import com.jgchk.hotelhavoc.features.game.OrderItem

class Hamburger(override var currentIngredients: Set<Ingredient> = setOf()) : OrderItem() {

    override val requiredIngredients = listOf(BottomBun, CookedPatty, ChoppedLettuce, TopBun)

    override fun isCombinableWith(item: OrderItem): Boolean {
        if (item !is Hamburger) return false

        if (currentIngredients.intersect(item.currentIngredients).isNotEmpty()) return false

        currentIngredients.union(item.currentIngredients).forEach { ingredient ->
            if (!requiredIngredients.contains(ingredient)) {
                return false
            }
        }

        return true
    }

    override fun combineWith(item: OrderItem): OrderItem {
        if (!isCombinableWith(item)) throw IllegalArgumentException()
        return Hamburger(currentIngredients.union(item.currentIngredients))
    }

    override fun drawables(): IntArray {
        return currentIngredients.map { it.drawable() }.toIntArray()
    }

    override fun toString() = "Hamburger: $currentIngredients"
}