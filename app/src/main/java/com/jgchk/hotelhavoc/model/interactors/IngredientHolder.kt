package com.jgchk.hotelhavoc.model.interactors

import com.jgchk.hotelhavoc.model.domain.Item
import com.jgchk.hotelhavoc.model.domain.ingredients.Lettuce
import com.jgchk.hotelhavoc.model.domain.ingredients.Patty
import com.jgchk.hotelhavoc.model.domain.ingredients.Tomato

class IngredientHolder {

    var currentItem: Item? = null

    fun tapItem(itemString: String): Item? {
        currentItem = toItem(itemString)
        return currentItem
    }

    private fun toItem(itemString: String): Item? {
        return when (itemString) {
            "lettuce" -> Lettuce()
            "tomato" -> Tomato()
            "raw patty" -> Patty()
            else -> null
        }
    }
}