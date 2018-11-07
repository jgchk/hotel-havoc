package com.jgchk.hotelhavoc.features.game

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.jgchk.hotelhavoc.core.platform.BaseViewModel
import javax.inject.Inject

class GameViewModel
@Inject constructor(private val gameController: GameController,
                    private val sensorProcessor: SensorProcessor) : BaseViewModel(), SensorEventListener {

    //    var orders: MutableLiveData<List<OrderView>> = gameController.ordersData
    val actionProgress: LiveData<Int> = gameController.actionProgressData
    val state: LiveData<GameState> = gameController.gameStateData
    val hand: LiveData<OrderItemView> = Transformations.map(gameController.handData) { orderItem -> convertHandDataToUIModel(orderItem) }

    private fun convertHandDataToUIModel(orderItem: OrderItem?): OrderItemView? {
        return orderItem?.let {
            OrderItemView(orderItem.currentIngredients.map { ingredient -> ingredient.name() }.toTypedArray(),
                    orderItem.currentIngredients
                            .sortedBy { ingredient -> ingredient.order() }
                            .map { ingredient -> ingredient.drawable() }
                            .toIntArray())
        }
    }

    fun onTapNfc(nfcString: String) = gameController.onTapNfc(nfcString)

    fun onActionCompleted() = gameController.onActionCompleted()

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorProcessor.isShakeGesture(sensorEvent)) {
            // TODO
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun onBeamIngredient() = gameController.onBeamIngredient()
}