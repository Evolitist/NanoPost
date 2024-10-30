package com.evolitist.nanopost.presentation.ui.view

import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillTree
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.ObserverModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.observeReads
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import kotlinx.collections.immutable.ImmutableList
import androidx.compose.ui.autofill.AutofillNode as UiAutofillNode

fun Modifier.autofill(
    autofillTypes: ImmutableList<AutofillType>,
    onFill: (String) -> Unit,
): Modifier = this then AutofillNodeElement(autofillTypes, onFill)

private class AutofillNode(
    private var autofillNode: UiAutofillNode,
) : Modifier.Node(),
    ObserverModifierNode,
    FocusEventModifierNode,
    GlobalPositionAwareModifierNode,
    CompositionLocalConsumerModifierNode
{
    private lateinit var autofillTree: AutofillTree

    override fun onAttach() {
        updateAutofillTree()
        observeReads { currentValueOf(LocalAutofillTree) }
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        autofillNode.boundingBox = coordinates.boundsInWindow()
    }

    override fun onFocusEvent(focusState: FocusState) {
        currentValueOf(LocalAutofill)?.run {
            if (focusState.isFocused) {
                requestAutofillForNode(autofillNode)
            } else {
                cancelAutofillForNode(autofillNode)
            }
        }
    }

    override fun onObservedReadsChanged() {
        updateAutofillTree()
    }

    fun updateAutofillNode(node: UiAutofillNode) {
        node.boundingBox = autofillNode.boundingBox
        autofillNode = node
    }

    private fun updateAutofillTree() {
        autofillTree = currentValueOf(LocalAutofillTree)
        autofillTree += autofillNode
    }
}

private data class AutofillNodeElement(
    val autofillTypes: ImmutableList<AutofillType>,
    val onFill: (String) -> Unit,
) : ModifierNodeElement<AutofillNode>() {

    override fun create(): AutofillNode {
        return AutofillNode(UiAutofillNode(autofillTypes, onFill = onFill))
    }

    override fun update(node: AutofillNode) {
        node.updateAutofillNode(UiAutofillNode(autofillTypes, onFill = onFill))
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "autofill"
        properties["autofillTypes"] = autofillTypes
        properties["onFill"] = onFill
    }
}
