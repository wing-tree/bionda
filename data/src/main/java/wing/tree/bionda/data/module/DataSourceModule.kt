package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.database.Database
import wing.tree.bionda.data.service.ForecastService
import wing.tree.bionda.data.source.local.NoticeDataSource
import javax.inject.Singleton
import wing.tree.bionda.data.source.local.ForecastDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.ForecastDataSource as RemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    @Singleton
    fun providesLocalDataSource(
        database: Database
    ): LocalDataSource {
        return LocalDataSource(database.forecastDao())
    }

    @Provides
    @Singleton
    fun providesNoticeDataSource(
        database: Database
    ): NoticeDataSource {
        return NoticeDataSource(database.noticeDao())
    }

    @Provides
    @Singleton
    fun providesRemoteDataSource(
        forecastService: ForecastService
    ): RemoteDataSource {
        return RemoteDataSource(forecastService)
    }
}
