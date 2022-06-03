package com.evolitist.nanopost.presentation.ui.image

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.TransformableState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.panBy
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.zoomBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.evolitist.nanopost.presentation.utils.OffsetSaver
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun rememberZoomableState(): ZoomableState {
    val zoom = rememberSaveable { mutableStateOf(1f) }
    val offset = rememberSaveable(stateSaver = OffsetSaver) {
        mutableStateOf(Offset.Zero)
    }

    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        zoom.value = (zoom.value * zoomChange).coerceIn(1f, 3f)
        offset.value += panChange
    }

    return remember { DefaultZoomableState(transformableState, zoom, offset) }
}

fun Modifier.zoomable(
    state: ZoomableState,
    onTap: (() -> Unit)? = null,
) = this.composed {
    val scope = rememberCoroutineScope()

    Modifier.pointerInput(Unit) {
        detectTransformGestures { centroid, pan, zoom, _ ->
            scope.launch {
                state.applyTransform(centroid, pan, zoom)
            }
        }
    }.pointerInput(Unit) {
        detectTapGestures(
            onTap = onTap?.let {
                { it() }
            },
            onDoubleTap = {
                scope.launch {
                    state.animateTransform(
                        centroid = it,
                        pan = Offset.Zero,
                        zoom = if (state.zoom.roundToInt() == 1) 3f else 1f.div(state.zoom),
                    )
                }
            },
        )
    }.graphicsLayer {
        translationX = -state.offset.x * state.zoom
        translationY = -state.offset.y * state.zoom
        scaleX = state.zoom
        scaleY = state.zoom
        transformOrigin = TransformOrigin(0f, 0f)
    }
}

@Stable
interface ZoomableState {
    val zoom: Float
    val offset: Offset

    suspend fun applyTransform(centroid: Offset, pan: Offset, zoom: Float)

    suspend fun animateTransform(centroid: Offset, pan: Offset, zoom: Float)
}

private class DefaultZoomableState(
    private val transformableState: TransformableState,
    private val zoomState: State<Float>,
    private val offsetState: State<Offset>,
) : ZoomableState {

    override val zoom: Float
        get() = zoomState.value

    override val offset: Offset
        get() = offsetState.value

    override suspend fun applyTransform(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
    ) {
        val oldScale = zoomState.value
        val newScale = (zoomState.value * zoom).coerceIn(1f, 3f)
        val gestureOffset = (centroid / oldScale) - (centroid / newScale + pan / oldScale)

        transformableState.zoomBy(zoom)
        transformableState.panBy(gestureOffset)
    }

    override suspend fun animateTransform(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
    ) {
        if (transformableState.isTransformInProgress) return

        transformableState.transform {
            val adjustedCentroid = centroid / zoomState.value
            var previousZoom = 1f

            AnimationState(initialValue = previousZoom).animateTo(zoom) {
                val scaleFactor = if (previousZoom == 0f) 1f else this.value / previousZoom
                transformBy(zoomChange = scaleFactor)
                val delta = (adjustedCentroid / previousZoom) - (adjustedCentroid / this.value + pan / previousZoom)
                transformBy(panChange = delta)
                previousZoom = this.value
            }
        }
    }
}
