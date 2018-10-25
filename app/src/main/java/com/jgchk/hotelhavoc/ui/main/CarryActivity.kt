package com.jgchk.hotelhavoc.ui.main

import android.app.PendingIntent
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jgchk.hotelhavoc.R
import com.jgchk.hotelhavoc.databinding.ActivityCarryBinding
import com.jgchk.hotelhavoc.util.NfcUtil

class CarryActivity : AppCompatActivity() {

    companion object {
        private val TAG = CarryActivity::class.qualifiedName
    }

    private lateinit var binding: ActivityCarryBinding
    private lateinit var viewModel: CarryViewModel
    private lateinit var nfcAdapter: NfcAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_carry)

        viewModel = ViewModelProviders.of(this).get(CarryViewModel::class.java)
        viewModel.itemName.observe(this, Observer {
            itemName -> itemName?.let { binding.textView.text = itemName }
        })
        viewModel.itemImage.observe(this, Observer {
            imageId -> imageId?.let { binding.imageView.setImageDrawable(resources.getDrawable(it, theme)) }
        })
        binding.viewModel = viewModel

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (!nfcAdapter.isEnabled) {
            // TODO: display some can't play message
        }
    }

    override fun onResume() {
        super.onResume()
        setupForegroundDispatch()
    }

    override fun onPause() {
        nfcAdapter.disableForegroundDispatch(this)
        super.onPause()
    }

    private fun setupForegroundDispatch() {
        val intent = Intent(applicationContext, CarryActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, intent.toString())
        if (intent?.action == NfcAdapter.ACTION_NDEF_DISCOVERED) {
            val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val tagString = NfcUtil.readTag(tag)
            viewModel.tapItem(tagString)
        }
    }
}
