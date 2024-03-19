package com.github.qrscanlib

import android.media.AudioManager
import android.media.ToneGenerator

private var confirmToneGenerator: ToneGenerator? = null
private var errorToneGenerator: ToneGenerator? = null

fun beepConfirm() {
	val tg = confirmToneGenerator ?: ToneGenerator(
		AudioManager.STREAM_NOTIFICATION,
		ToneGenerator.MAX_VOLUME
	)
	confirmToneGenerator = tg
//	fun beepTone() = when (beepToneName) {
//		"tone_cdma_confirm" -> ToneGenerator.TONE_CDMA_CONFIRM
//		"tone_sup_radio_ack" -> ToneGenerator.TONE_SUP_RADIO_ACK
//		"tone_prop_ack" -> ToneGenerator.TONE_PROP_ACK
//		"tone_prop_beep" -> ToneGenerator.TONE_PROP_BEEP
//		"tone_prop_beep2" -> ToneGenerator.TONE_PROP_BEEP2
//		else -> ToneGenerator.TONE_PROP_BEEP
//	}
	tg.startTone(ToneGenerator.TONE_PROP_BEEP)
}

fun beepError() {
	val tg = errorToneGenerator ?: ToneGenerator(
		AudioManager.STREAM_ALARM,
		ToneGenerator.MAX_VOLUME
	)
	errorToneGenerator = tg
	tg.startTone(ToneGenerator.TONE_SUP_ERROR, 1000)
}

fun releaseToneGenerators() {
	confirmToneGenerator?.release()
	confirmToneGenerator = null
	errorToneGenerator?.release()
	errorToneGenerator = null
}
