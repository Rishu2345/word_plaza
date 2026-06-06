package com.buildsol.wordplaza.view.profileChange

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.min
import androidx.core.graphics.scale

class ProfileImageProcessor(
    private val outputSizePx: Int = 512,
    private val jpegQuality: Int = 88
) {
    suspend fun bytesFromUri(context: Context, uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        val source = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        } ?: error("Unable to read the selected image.")

        source.toProfileJpegBytes()
    }

    suspend fun bytesFromBitmap(bitmap: Bitmap): ByteArray = withContext(Dispatchers.Default) {
        bitmap.toProfileJpegBytes()
    }

    private fun Bitmap.toProfileJpegBytes(): ByteArray {
        val squareSize = min(width, height)
        require(squareSize > 0) { "Selected image is empty." }

        val left = (width - squareSize) / 2
        val top = (height - squareSize) / 2
        val cropped = Bitmap.createBitmap(this, left, top, squareSize, squareSize)
        val scaled = if (cropped.width == outputSizePx && cropped.height == outputSizePx) {
            cropped
        } else {
            cropped.scale(outputSizePx, outputSizePx)
        }

        return ByteArrayOutputStream().use { outputStream ->
            scaled.compress(Bitmap.CompressFormat.JPEG, jpegQuality, outputStream)
            outputStream.toByteArray()
        }
    }
}
