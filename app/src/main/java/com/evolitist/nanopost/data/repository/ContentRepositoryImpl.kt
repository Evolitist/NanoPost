package com.evolitist.nanopost.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.annotation.WorkerThread
import androidx.core.graphics.decodeBitmap
import com.evolitist.nanopost.data.extensions.bound
import com.evolitist.nanopost.data.extensions.use
import com.evolitist.nanopost.domain.model.ImageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class ContentRepositoryImpl @Inject constructor(
    private val contentResolver: ContentResolver,
    private val mimeTypeMap: MimeTypeMap,
) : ContentRepository {

    companion object {
        private const val MaxImageSize = 2560
        private const val DefaultFileName = "image"
    }

    override suspend fun resolveUri(contentUri: Uri): ImageInfo = withContext(Dispatchers.Default) {
        ByteArrayOutputStream().use { stream ->
            val mimeType = contentResolver.getType(contentUri)
            val fileName = contentResolver.getFileName(contentUri) ?: getDefaultFileName(mimeType)
            loadBitmap(contentUri).bound(MaxImageSize).use {
                it.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            }
            ImageInfo(
                fileName,
                mimeType,
                stream.toByteArray(),
            )
        }
    }

    private fun ContentResolver.getFileName(uri: Uri): String? {
        return query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        }
    }

    @Suppress("DEPRECATION")
    @WorkerThread
    private fun loadBitmap(source: Uri): Bitmap {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.createSource(contentResolver, source).decodeBitmap { _, _ -> }
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, source)
        }
        return bitmap
    }

    private fun getDefaultFileName(mimeType: String?): String {
        val extension = mimeTypeMap.getExtensionFromMimeType(mimeType)
        return "$DefaultFileName.$extension"
    }
}
