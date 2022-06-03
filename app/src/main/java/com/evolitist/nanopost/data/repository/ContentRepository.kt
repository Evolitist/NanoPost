package com.evolitist.nanopost.data.repository

import android.net.Uri
import com.evolitist.nanopost.domain.model.ImageInfo

interface ContentRepository {

    suspend fun resolveUri(contentUri: Uri): ImageInfo
}
