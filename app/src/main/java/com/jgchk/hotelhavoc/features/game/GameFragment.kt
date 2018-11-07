package com.jgchk.hotelhavoc.features.game

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.LayerDrawable
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import android.view.View
import com.jgchk.hotelhavoc.R
import com.jgchk.hotelhavoc.core.exception.Failure
import com.jgchk.hotelhavoc.core.extension.failure
import com.jgchk.hotelhavoc.core.extension.observe
import com.jgchk.hotelhavoc.core.extension.viewModel
import com.jgchk.hotelhavoc.core.platform.BaseFragment
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : BaseFragment(), NfcAdapter.OnNdefPushCompleteCallback {
    override fun onNdefPushComplete(p0: NfcEvent?) = gameViewModel.onBeamIngredient()

    companion object {
        private val TAG = GameFragment::class.qualifiedName
    }

    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var gameViewModel: GameViewModel

//    val sensorListener: SensorEventListener = gameViewModel

    override fun layoutId() = R.layout.fragment_game

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        gameViewModel = viewModel(viewModelFactory) {
            observe(hand, ::renderHand)
            observe(state, ::renderGameState)
            observe(actionProgress, ::renderActionProgress)
            failure(failure, ::handleFailure)
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(activity)
        nfcAdapter.setOnNdefPushCompleteCallback(this, activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        action_btn.setOnClickListener { gameViewModel.onActionCompleted() }
    }

    fun onTapNfc(nfcString: String) {
        gameViewModel.onTapNfc(nfcString)
    }

//    override fun onSensorChanged(sensorEvent: SensorEvent) {
//        try {
//            gameViewModel.onSensorChanged(sensorEvent)
//        } catch (e: UninitializedPropertyAccessException) {
//        }
//    }

//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun renderHand(item: OrderItemView?) {
        setBeamMessage(item?.ingredientStrings?.joinToString() ?: "none")

        if (item == null) {
            ingredient_view.setImageDrawable(null)
        } else {
            val layerDrawable = LayerDrawable(item.ingredientDrawables.map { resources.getDrawable(it) }.toTypedArray())
            for (i in 0 until layerDrawable.numberOfLayers) {
                layerDrawable.setLayerInset(i, 0, 0, 0, i * 10)
            }
            ingredient_view.setImageDrawable(layerDrawable)
        }
    }

    fun setBeamMessage(nfcString: String) {
        nfcAdapter.setNdefPushMessage(NdefMessage(NdefRecord.createTextRecord("en", nfcString)), activity)
    }

    private fun renderGameState(gameState: GameState?) {
        when (gameState) {
            GameState.NORMAL, null -> {
                action_btn.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
            }
            GameState.CHOP -> {
                action_btn.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                action_btn.text = "CHOP"
            }
            GameState.COOK -> {
                action_btn.visibility = View.VISIBLE
                progressBar.visibility = View.VISIBLE
                action_btn.text = "COOK"
            }
        }
    }

    private fun renderActionProgress(progress: Int?) {
        progressBar.progress = progress ?: 0
    }

    private fun handleFailure(failure: Failure?) {
        // TODO
    }

    override fun onResume() {
        super.onResume()
        enableForegroundDispatch()
    }

    override fun onPause() {
        super.onPause()
        disableForegroundDispatch()
    }

    fun enableForegroundDispatch() {
        val intent = Intent(activity!!.applicationContext, activity!!.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(activity!!.applicationContext, 0, intent, 0)
        nfcAdapter.enableForegroundDispatch(activity, pendingIntent, null, null)
    }

    fun disableForegroundDispatch() {
        nfcAdapter.disableForegroundDispatch(activity)
    }
}