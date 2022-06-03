package com.evolitist.nanopost.presentation.extensions

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

fun EnterTransition.unless(condition: () -> Boolean): EnterTransition {
    return if (condition()) {
        this
    } else {
        EnterTransition.None
    }
}

fun ExitTransition.unless(condition: () -> Boolean): ExitTransition {
    return if (condition()) {
        this
    } else {
        ExitTransition.None
    }
}
