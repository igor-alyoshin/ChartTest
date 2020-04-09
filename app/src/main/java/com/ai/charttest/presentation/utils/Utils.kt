package com.ai.charttest.presentation.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.FileProvider
import com.ai.charttest.BuildConfig
import com.ai.charttest.presentation.App
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import kotlin.reflect.jvm.internal.impl.load.java.UtilsKt

inline fun <T, R> withNotNull(receiver: T?, block: T.() -> R): R? {
    return receiver?.block()
}

fun Activity.showKeyboard(view: View?) {
    view?.requestFocus()
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, 0)
}

fun Activity.hideKeyboard(view: View? = null) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow((view ?: currentFocus ?: View(this)).windowToken, 0)
}

fun String.fromBase64(): String? {
    try {
        return String(Base64.decode(this, Base64.DEFAULT), Charsets.UTF_8)
    } catch (e: IllegalArgumentException) {
        return null
    }
}

fun getString(resId: Int) = App.instance.resources.getString(resId)

fun View.snapshot(): Bitmap {
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    draw(Canvas(result))
    return result
}

fun shareBitmap(context: Context, bitmap: Bitmap) {
    try {
        val file = File(context.cacheDir, "screenshot.png")
        val fOut = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        fOut.flush()
        fOut.close()
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val uri =
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/png"
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e(UtilsKt::class.java.simpleName, "Errow when share bitmap", e)
    }
}