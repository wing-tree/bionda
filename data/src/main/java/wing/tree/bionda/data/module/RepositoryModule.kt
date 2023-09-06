package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.processor.PostProcessor
import wing.tree.bionda.data.repository.AlarmRepository
import wing.tree.bionda.data.repository.LivingWthrIdxRepository
import wing.tree.bionda.data.repository.WeatherRepository
import wing.tree.bionda.data.source.local.AlarmDataSource
import wing.tree.bionda.data.source.local.AreaDataSource
import wing.tree.bionda.data.source.local.LivingWthrIdxDataSource as LocalLivingWthrIdxDataSource
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.LivingWthrIdxDataSource as RemoteLivingWthrIdxDataSource
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
    fun providesLivingWthrIdxRepository(
        areaDataSource: AreaDataSource,
        localDataSource: LocalLivingWthrIdxDataSource,
        remoteDataSource: RemoteLivingWthrIdxDataSource
    ): LivingWthrIdxRepository {
        return LivingWthrIdxRepository(areaDataSource, localDataSource, remoteDataSource)
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
