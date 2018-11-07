package com.jgchk.hotelhavoc.features.game

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import com.jgchk.hotelhavoc.R
import com.jgchk.hotelhavoc.core.platform.BaseActivity
import kotlin.experimental.and

class GameActivity : BaseActivity() {

    companion object {
        private val TAG = GameActivity::class.qualifiedName
        fun callingIntent(context: Context) = Intent(context, GameActivity::class.java)
    }

//    private lateinit var sensorManager: SensorManager

    override fun fragment() = GameFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        sensorManager.registerListener(fragment(), accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, intent.toString())
        onNfcScan(intent)
    }

    private fun onNfcScan(intent: Intent?) {
        val nfcString = intent?.let { readNfcIntent(it) }
        nfcString?.let { (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as GameFragment).onTapNfc(it) }
    }

    fun readNfcIntent(intent: Intent): String? {
        if (intent.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            return readTag(intent.getParcelableExtra(NfcAdapter.EXTRA_TAG))
        }
        return null
    }

    private fun readTag(tag: Tag): String {
        val ndef = Ndef.get(tag)
        val ndefMessage = ndef.cachedNdefMessage
        val records = ndefMessage.records
        return readText(records[0])
    }

    private fun readText(record: NdefRecord): String {
        val languageCodeLength = record.payload[0] and 51
        return String(record.payload, languageCodeLength + 1, record.payload.size - languageCodeLength - 1)
    }
}