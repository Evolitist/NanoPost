package com.evolitist.nanopost.di

import com.evolitist.nanopost.data.repository.AuthRepository
import com.evolitist.nanopost.data.repository.AuthRepositoryImpl
import com.evolitist.nanopost.data.repository.ContentRepository
import com.evolitist.nanopost.data.repository.ContentRepositoryImpl
import com.evolitist.nanopost.data.repository.ImageRepository
import com.evolitist.nanopost.data.repository.ImageRepositoryImpl
import com.evolitist.nanopost.data.repository.PostRepository
import com.evolitist.nanopost.data.repository.PostRepositoryImpl
import com.evolitist.nanopost.data.repository.ProfileRepository
import com.evolitist.nanopost.data.repository.ProfileRepositoryImpl
import com.evolitist.nanopost.data.repository.ResourcesRepository
import com.evolitist.nanopost.data.repository.ResourcesRepositoryImpl
import com.evolitist.nanopost.data.repository.SettingsRepository
import com.evolitist.nanopost.data.repository.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindContentRepository(impl: ContentRepositoryImpl): ContentRepository

    @Binds
    abstract fun bindImageRepository(impl: ImageRepositoryImpl): ImageRepository

    @Binds
    abstract fun bindPostRepository(impl: PostRepositoryImpl): PostRepository

    @Binds
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    abstract fun bindResourcesRepository(impl: ResourcesRepositoryImpl): ResourcesRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl,
    ): SettingsRepository
}
