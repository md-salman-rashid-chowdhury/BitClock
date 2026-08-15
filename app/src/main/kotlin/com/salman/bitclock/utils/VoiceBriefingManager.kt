package com.salman.bitclock.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceBriefingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            isInitialized = true
        }
    }

    fun speakBriefing(label: String) {
        if (!isInitialized) return

        val now = Calendar.getInstance()
        val timeString = "${now.get(Calendar.HOUR_OF_DAY)} ${now.get(Calendar.MINUTE)}"
        
        val briefing = "Good morning! It's $timeString. Your alarm $label just went off. Today is expected to be sunny."
        
        tts?.speak(briefing, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
