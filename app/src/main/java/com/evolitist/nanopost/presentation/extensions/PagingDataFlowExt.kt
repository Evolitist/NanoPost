package com.evolitist.nanopost.presentation.extensions

import androidx.paging.PagingData
import androidx.paging.TerminalSeparatorType
import androidx.paging.insertHeaderItem
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

fun <T: Any, R: Any> Flow<PagingData<T>>.mapData(transform: suspend (T) -> R) = map { it.map(transform) }

fun <T: Any> Flow<PagingData<T>>.withHeader(
    terminalSeparatorType: TerminalSeparatorType = TerminalSeparatorType.FULLY_COMPLETE,
    producer: suspend () -> Flow<T>,
) = flatMapLatest { pagingData ->
    producer().map { header ->
        pagingData.insertHeaderItem(terminalSeparatorType, header)
    }
}

fun <T: Any> Flow<PagingData<T>>.withHeaders(
    terminalSeparatorType: TerminalSeparatorType = TerminalSeparatorType.FULLY_COMPLETE,
    producer: suspend () -> Flow<List<T>>,
) = flatMapLatest { pagingData ->
    producer().map { headers ->
        headers.asReversed().fold(pagingData) { acc, header ->
            acc.insertHeaderItem(terminalSeparatorType, header)
        }
    }
}
