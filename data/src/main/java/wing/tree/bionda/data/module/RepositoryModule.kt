package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.repository.ForecastRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.data.source.local.NoticeDataSource
import javax.inject.Singleton
import wing.tree.bionda.data.source.local.ForecastDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.ForecastDataSource as RemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providesForecastRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): ForecastRepository {
        return ForecastRepository(localDataSource, remoteDataSource)
    }

    @Provides
    @Singleton
    fun providesNoticeRepository(
        noticeDataSource: NoticeDataSource
    ): NoticeRepository {
        return NoticeRepository(noticeDataSource)
    }
}
