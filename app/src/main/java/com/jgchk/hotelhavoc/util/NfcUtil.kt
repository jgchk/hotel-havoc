package com.jgchk.hotelhavoc.util

import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import kotlin.experimental.and

class NfcUtil {
    companion object {
        fun readTag(tag: Tag): String {
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
}
