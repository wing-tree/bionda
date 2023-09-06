package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.processor.PostProcessor
import wing.tree.bionda.data.provider.AreaNoProvider
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.LivingWthrIdxRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.data.source.local.AlarmDataSource
import wing.tree.bionda.data.source.local.LivingWthrIdxDataSource as LivingWthrIdxLocalDataSource
import wing.tree.bionda.data.source.local.WeatherDataSource as WeatherLocalDataSource
import wing.tree.bionda.data.source.remote.LivingWthrIdxDataSource as LivingWthrIdxRemoteDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as WeatherRemoteDataSource

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
    fun providesLivingWthrIdxRepository(
        localDataSource: LivingWthrIdxLocalDataSource,
        remoteDataSource: LivingWthrIdxRemoteDataSource,
        areaNoProvider: AreaNoProvider
    ): LivingWthrIdxRepository {
        return LivingWthrIdxRepository(localDataSource, remoteDataSource, areaNoProvider)
    }

    @Provides
    fun providesForecastRepository(
        localDataSource: WeatherLocalDataSource,
        remoteDataSource: WeatherRemoteDataSource,
        postProcessor: PostProcessor
    ): WeatherRepository {
        return WeatherRepository(localDataSource, remoteDataSource, postProcessor)
    }
}
