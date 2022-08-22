package com.evolitist.nanopost.domain.mapper

import com.evolitist.nanopost.data.network.model.ApiImage
import com.evolitist.nanopost.domain.model.Image
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class ImageMapper @Inject constructor(
    private val profileCompactMapper: ProfileCompactMapper,
    private val imageSizeMapper: ImageSizeMapper,
) {
    fun fromApiToModel(api: ApiImage) = Image(
        id = api.id,
        owner = api.owner.let(profileCompactMapper::fromApiToModel),
        dateCreated = api.dateCreated,
        sizes = api.sizes.map(imageSizeMapper::fromApiToModel).toImmutableList(),
    )
}
