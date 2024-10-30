package com.evolitist.nanopost.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ApiUsernameCheckResult {
    @SerialName("TooShort") TooShort,
    @SerialName("TooLong") TooLong,
    @SerialName("InvalidCharacters") InvalidCharacters,
    @SerialName("Taken") Taken,
    @SerialName("Free") Free,
}
