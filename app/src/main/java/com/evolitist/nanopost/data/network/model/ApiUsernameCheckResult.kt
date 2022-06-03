package com.evolitist.nanopost.data.network.model

enum class ApiUsernameCheckResult {
    TooShort,
    TooLong,
    InvalidCharacters,
    Taken,
    Free,
}
