package com.evolitist.nanopost.domain.model

class ImageInfo(
    val fileName: String,
    val mimeType: String?,
    val bytes: ByteArray,
) {
    override fun equals(other: Any?) = other is ImageInfo && other.fileName == fileName
    override fun hashCode() = fileName.hashCode()
}
