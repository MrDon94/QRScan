package com.github.qrscanlib

import android.content.Context
import android.media.AudioManager

fun Context.scanFeedback() {
	getVibrator().vibrate()
	if (!isSilent()) {
		beepConfirm()
	}
}

fun Context.errorFeedback() {
	getVibrator().error()
	if (!isSilent()) {
		beepError()
	}
}

private fun Context.isSilent(): Boolean {
	val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
	return when (am.ringerMode) {
		AudioManager.RINGER_MODE_SILENT,
		AudioManager.RINGER_MODE_VIBRATE -> true

		else -> false
	}
}
