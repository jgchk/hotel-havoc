package com.jgchk.hotelhavoc.model.domain

abstract class Order(
    override val name: String,
    val requiredIngredients: Set<Ingredient>,
    val currentIngredients: Set<Ingredient>
) : Item