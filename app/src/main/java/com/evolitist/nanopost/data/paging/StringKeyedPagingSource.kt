package com.evolitist.nanopost.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.evolitist.nanopost.data.network.model.response.PagedDataResponse
import kotlinx.coroutines.CancellationException

class StringKeyedPagingSource<T : Any>(
    private val dataLoader: suspend (Int, String?) -> PagedDataResponse<T>,
) : PagingSource<String, T>() {

    override suspend fun load(params: LoadParams<String>) = try {
        val response = dataLoader(params.loadSize, params.key)
        LoadResult.Page(
            data = response.items,
            prevKey = null,
            nextKey = response.offset.takeIf { response.count >= params.loadSize },
        )
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<String, T>): String? = null
}
