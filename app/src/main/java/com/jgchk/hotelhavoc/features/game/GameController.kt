package com.jgchk.hotelhavoc.features.game

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.jgchk.hotelhavoc.features.game.items.*
import javax.inject.Inject

class GameController
@Inject constructor(private val getChopSensorReading: GetChopSensorReading) {

    companion object {
        private val TAG = GameController::class.qualifiedName
    }

    val gameStateData = MutableLiveData<GameState>().apply { postValue(GameState.NORMAL) }
    private var gameState: GameState
        get() = gameStateData.value!!
        set(value) {
            gameStateData.value = value
        }

    val actionProgressData = MutableLiveData<Int>().apply { postValue(0) }
    private var actionProgress: Int
        get() = actionProgressData.value!!
        set(value) {
            actionProgressData.postValue(value)
        }

    val ordersData = MutableLiveData<List<Order>>().apply { postValue(listOf()) }
    private var orders: List<Order>
        get() = ordersData.value!!
        set(value) {
            ordersData.postValue(value)
        }

    val handData = MutableLiveData<OrderItem?>().apply { postValue(null) }
    private var hand: OrderItem?
        get() = handData.value
        set(value) {
            handData.postValue(value)
        }

    fun onTapNfc(nfcString: String) {
        when (nfcString) {
            "chop" -> {
                if (hand?.isChoppable() == true) gameState = GameState.CHOP
            }
            "cook" -> {
                if (hand?.isCookable() == true) gameState = GameState.COOK
            }
            else -> {
                gameState = GameState.NORMAL
                val orderItem = nfcTagToOrderItem(nfcString)
                hand = when {
                    orderItem == null -> {
                        Log.d(TAG, "Received nothing. Doing nothing.")
                        hand
                    }
                    hand == null -> {
                        Log.d(TAG, "Not holding anything. Adding to hand...")
                        orderItem
                    }
                    hand!!.isCombinableWith(orderItem) -> {
                        Log.d(TAG, "Combinable!")
                        hand!!.combineWith(orderItem)
                    }
                    else -> {
                        Log.d(TAG, "Not combinable :(")
                        orderItem
                    }
                }
                Log.d(TAG, hand.toString())
            }
        }
    }

    fun onActionCompleted() {
        actionProgress += 10
        if (actionProgress >= 100) {
            when (gameState) {
                GameState.CHOP -> hand = Hamburger(setOf(ChoppedLettuce))
                GameState.COOK -> hand = Hamburger(setOf(CookedPatty))
            }
            gameState = GameState.NORMAL
            actionProgress = 0
        }
    }

    private val ingredientMap = listOf(BottomBun, TopBun, RawPatty, CookedPatty, Lettuce, ChoppedLettuce).associateBy({ it.name() }, { it })

    fun nfcTagToOrderItem(nfcString: String): OrderItem? {
        val ingredientStrings = nfcString.split(",").map { it.trim() }
        if (ingredientStrings.size > 1) {
            return Hamburger(ingredientStrings.map { ingredientMap[it] }.toSet() as Set<Ingredient>)
        }

        return when {
            nfcString == "none" -> null
            nfcString == "bun" -> Hamburger(setOf(BottomBun, TopBun))
            ingredientMap.containsKey(nfcString) -> Hamburger(setOf(ingredientMap[nfcString]!!))
            else -> throw IllegalArgumentException("Illegal NFC string $nfcString")
        }
    }

    fun onBeamIngredient() {
        hand = null
    }
}