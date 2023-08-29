package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.core.PostProcessor
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.data.source.local.AlarmDataSource
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as RemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun providesAlarmRepository(
        alarmDataSource: AlarmDataSource
    ): AlarmRepository {
        return AlarmRepository(alarmDataSource)
    }

    @Provides
    fun providesForecastRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource,
        postProcessor: PostProcessor
    ): WeatherRepository {
        return WeatherRepository(localDataSource, remoteDataSource, postProcessor)
    }
}
