package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    autofillTypes: ImmutableList<AutofillType>,
    onFill: (String) -> Unit,
) = composed {
    val onFillState by rememberUpdatedState(onFill)

    val autofill = LocalAutofill.current
    val autofillNode = remember(autofillTypes) {
        AutofillNode(onFill = onFillState, autofillTypes = autofillTypes)
    }
    LocalAutofillTree.current += autofillNode

    this.onGloballyPositioned {
        autofillNode.boundingBox = it.boundsInWindow()
    }.onFocusChanged { focusState ->
        autofill?.run {
            if (focusState.isFocused) {
                requestAutofillForNode(autofillNode)
            } else {
                cancelAutofillForNode(autofillNode)
            }
        }
    }
}
