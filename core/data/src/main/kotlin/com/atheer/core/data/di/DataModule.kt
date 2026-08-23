package com.atheer.core.data.di

import com.atheer.core.common.di.ApplicationScope
import com.atheer.core.data.SystemTimeSource
import com.atheer.core.data.TimeSource
import com.atheer.core.data.VideoRepository
import com.atheer.core.data.OfflineFirstVideoRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataBindsModule {

    @Binds
    abstract fun bindsVideoRepository(impl: OfflineFirstVideoRepository): VideoRepository

    @Binds
    abstract fun bindsTimeSource(impl: SystemTimeSource): TimeSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object DataProvidesModule {

    @Provides
    @Singleton
    @ApplicationScope
    fun providesApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
