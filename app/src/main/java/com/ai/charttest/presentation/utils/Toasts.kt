package com.ai.charttest.presentation.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.ai.charttest.presentation.App

private var toast: Toast? = null

fun showToast(stringResId: Int) {
    Handler(Looper.getMainLooper()).post {
        toast?.cancel()
        toast = Toast.makeText(App.instance, stringResId, Toast.LENGTH_LONG)
        toast?.show()
    }
}

fun showToast(s: CharSequence) {
    Handler(Looper.getMainLooper()).post {
        toast?.cancel()
        toast = Toast.makeText(App.instance, s, Toast.LENGTH_LONG)
        toast?.show()
    }
}

fun showToast(error: Throwable) {
    Handler(Looper.getMainLooper()).post {
        error.message?.also {
            toast?.cancel()
            toast = Toast.makeText(App.instance, it, Toast.LENGTH_LONG)
            toast?.show()
        }
    }
}