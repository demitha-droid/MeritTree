package com.example.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

object MindfulSoundHelper {

    private val scope = CoroutineScope(Dispatchers.Default)

    private const val SAMPLE_RATE = 44100
    private const val DURATION_SECONDS = 1.8

    // Precomputed once lazily in background so play is instant with 0 allocations
    private val precomputedSamples: ShortArray by lazy {
        val numSamples = (SAMPLE_RATE * DURATION_SECONDS).toInt()
        val samples = ShortArray(numSamples)
        val freqFundamental = 432.0
        val freqHarmonic = 864.0

        for (i in 0 until numSamples) {
            val time = i.toDouble() / SAMPLE_RATE
            val envelope = exp(-2.8 * time)
            val wave = (0.75 * sin(2.0 * Math.PI * freqFundamental * time)) +
                    (0.25 * sin(2.0 * Math.PI * freqHarmonic * time))
            val sampleVal = (wave * envelope * Short.MAX_VALUE * 0.45).toInt()
            samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        samples
    }

    /**
     * Synthesizes and plays a peaceful Tibetan singing bowl / temple bell tone.
     * Uses 432Hz fundamental with gentle 864Hz harmonic overtone and exponential decay.
     */
    fun playSingingBowlChime() {
        scope.launch {
            try {
                val samples = precomputedSamples
                val bufferSize = samples.size * 2
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(samples, 0, samples.size)
                audioTrack.play()

                // Clean up track after play finishes
                kotlinx.coroutines.delay((DURATION_SECONDS * 1000).toLong() + 200)
                audioTrack.release()
            } catch (_: Exception) {
                // Ignore any audio device issues gracefully
            }
        }
    }

    /**
     * Gentle haptic pulse for tapping leaves or recording merits.
     */
    fun performMindfulHaptic(context: Context, strong: Boolean = false) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val effect = if (strong) {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                } else {
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                }
                vibrator?.vibrate(effect)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createOneShot(
                    if (strong) 40 else 20,
                    if (strong) VibrationEffect.DEFAULT_AMPLITUDE else 80
                )
                vibrator?.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(if (strong) 40 else 20)
            }
        } catch (_: Exception) {
            // Ignore if vibration unavailable
        }
    }
}
