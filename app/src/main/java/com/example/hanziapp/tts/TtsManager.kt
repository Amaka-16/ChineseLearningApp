package com.example.hanziapp.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context.applicationContext, this)
    private var ready = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale.CHINESE)
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "Chinese TTS not available on this device")
                ready = false
            } else {
                ready = true
                Log.d("TtsManager", "Chinese TTS ready")
            }
        } else {
            Log.e("TtsManager", "TTS initialization failed with status $status")
            ready = false
        }
    }

    fun speak(text: String) {
        if (!ready) {
            Log.w("TtsManager", "speak() called but TTS not ready")
            return
        }
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "hanzi_utterance")
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}