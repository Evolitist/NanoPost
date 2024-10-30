package com.evolitist.nanopost.presentation.utils

import android.os.Parcelable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2

val OffsetSaver = Saver<Offset, Long>(
    save = { packFloats(it.x, it.y) },
    restore = { Offset(unpackFloat1(it), unpackFloat2(it)) },
)

fun <T : Parcelable> ParcelableListSaver() = listSaver<SnapshotStateList<T>, T>(
    save = { it.toList() },
    restore = { it.toMutableStateList() },
)
