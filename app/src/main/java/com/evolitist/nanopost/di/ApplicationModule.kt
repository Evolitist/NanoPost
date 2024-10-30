package com.evolitist.nanopost.di

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.webkit.MimeTypeMap
import androidx.work.WorkManager
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Reusable
    fun provideResources(@ApplicationContext context: Context): Resources {
        return context.resources
    }

    @Provides
    @Reusable
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver {
        return context.contentResolver
    }

    @Provides
    @Reusable
    fun provideMimeTypeMap(): MimeTypeMap {
        return MimeTypeMap.getSingleton()
    }

    @Provides
    @Reusable
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
}
