package com.evolitist.nanopost.data.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ObjectResponse<T>(val result: T)
