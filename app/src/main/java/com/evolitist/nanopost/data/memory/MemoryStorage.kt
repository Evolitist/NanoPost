package com.evolitist.nanopost.data.memory

import com.evolitist.nanopost.domain.model.Likes
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryStorage @Inject constructor() {

    private val likesCache = MutableStateFlow<PersistentMap<String, Likes>>(persistentMapOf())

    fun cacheLikes(postId: String, likes: Likes) {
        likesCache.update {
            it.put(postId, likes)
        }
    }

    fun getLikes(postId: String): Flow<Likes> {
        return likesCache.mapNotNull { it[postId] }
    }
}
