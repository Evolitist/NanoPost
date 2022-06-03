package com.evolitist.nanopost.domain.model

enum class UsernameCheckResult(val isError: Boolean) {
    TooShort(true),
    TooLong(true),
    InvalidCharacters(true),
    Taken(false),
    Free(false),
}
