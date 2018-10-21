package com.jgchk.hotelhavoc.ui.main

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.jgchk.hotelhavoc.base.BaseViewModel
import com.jgchk.hotelhavoc.model.interactors.IngredientHolder

class CarryViewModel : BaseViewModel() {

    companion object {
        private val TAG = CarryViewModel::class.qualifiedName
    }

    val itemName = MutableLiveData<String>()
    val itemImage = MutableLiveData<Int>()
    private val ingredientHolder = IngredientHolder()

    fun tapItem(itemString: String) {
        val currentItem = ingredientHolder.tapItem(itemString)
        Log.d(TAG, "Item set to: $itemString")
        itemName.value = currentItem?.name
        itemImage.value = currentItem?.image
    }
}