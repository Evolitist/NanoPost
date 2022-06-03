package com.evolitist.nanopost.data.repository

import android.content.res.Resources
import javax.inject.Inject

class ResourcesRepositoryImpl @Inject constructor(
    private val resources: Resources,
) : ResourcesRepository {

}
