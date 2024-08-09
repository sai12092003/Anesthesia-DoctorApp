package com.example.mohit

import io.flutter.embedding.android.FlutterActivity

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.annotation.NonNull
import io.flutter.embedding.engine.FlutterEngine

import io.flutter.plugin.common.MethodChannel
import java.util.*

class MainActivity: FlutterActivity() {
    private val CHANNEL = "samples.flutter.dev/tts"
    private lateinit var tts: TextToSpeech

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        tts = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                tts.language = Locale.US // Default to English (United States)
            }
        }
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "speak") {
                val text = call.argument<String>("text")
                val language = call.argument<String>("language")
                text?.let { txt ->
                    language?.let { lang ->
                        tts.language = Locale(lang.split('-')[0], lang.split('-')[1]) // Set the specified language
                        tts.speak(txt, TextToSpeech.QUEUE_FLUSH, null, "")
                        result.success(true)
                    } ?: result.error("LANGUAGE_NULL", "Language is null", null)
                } ?: result.error("TEXT_NULL", "Text is null", null)
            } else {
                result.notImplemented()
            }
        }
    }

    override fun onDestroy() {
        if (tts.isSpeaking) {
            tts.stop()
        }
        tts.shutdown()
        super.onDestroy()
    }
}