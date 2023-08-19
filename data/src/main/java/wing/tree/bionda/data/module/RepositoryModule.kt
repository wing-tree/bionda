package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.data.repository.NoticeRepository
import wing.tree.bionda.data.source.local.NoticeDataSource
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as RemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun providesForecastRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ): WeatherRepository {
        return WeatherRepository(localDataSource, remoteDataSource)
    }

    @Provides
    fun providesNoticeRepository(
        noticeDataSource: NoticeDataSource
    ): NoticeRepository {
        return NoticeRepository(noticeDataSource)
    }
}
