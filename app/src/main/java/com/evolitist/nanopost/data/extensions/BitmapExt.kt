package com.evolitist.nanopost.data.extensions

import android.graphics.Bitmap
import androidx.core.graphics.scale
import kotlin.math.roundToInt

fun <R> Bitmap.use(block: (Bitmap) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        if (exception == null) {
            recycle()
        } else try {
            recycle()
        } catch (closeException: Throwable) {
            exception.addSuppressed(closeException)
        }
    }
}

fun Bitmap.bound(targetSize: Int): Bitmap {
    val inWidth = this.width
    val inHeight = this.height
    val (outWidth, outHeight) = when {
        inWidth <= targetSize && inHeight <= targetSize -> {
            inWidth to inHeight
        }
        inWidth > inHeight -> {
            targetSize to (inHeight * targetSize.toFloat() / inWidth).roundToInt()
        }
        else -> {
            (inWidth * targetSize.toFloat() / inHeight).roundToInt() to targetSize
        }
    }
    return scale(outWidth, outHeight).also {
        if (!this.sameAs(it)) {
            this.recycle()
        }
    }
}
