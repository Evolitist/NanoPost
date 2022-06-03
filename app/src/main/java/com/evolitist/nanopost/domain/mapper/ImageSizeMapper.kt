package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.data.network.model.ApiImageSize
import com.evolitist.nanopost.domain.model.ImageSize
import javax.inject.Inject

class ImageSizeMapper @Inject constructor() {

    fun fromApiToModel(api: ApiImageSize) = ImageSize(
        width = api.width,
        height = api.height,
        url = api.url,
    )
}
