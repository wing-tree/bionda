package wing.tree.bionda.data.module

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import wing.tree.bionda.data.database.Database
import wing.tree.bionda.data.service.MidFcstInfoService
import wing.tree.bionda.data.service.VilageFcstInfoService
import wing.tree.bionda.data.source.local.AlarmDataSource
import wing.tree.bionda.data.source.local.WeatherDataSource as LocalDataSource
import wing.tree.bionda.data.source.remote.WeatherDataSource as RemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun providesAlarmDataSource(
        database: Database
    ): AlarmDataSource {
        return AlarmDataSource(database.alarmDao())
    }

    @Provides
    fun providesLocalDataSource(
        database: Database
    ): LocalDataSource {
        return LocalDataSource(
            midLandFcstDao = database.midLandFcstDao(),
            midTaDao = database.midTaDao(),
            vilageFcstDao = database.vilageFcstDao()
        )
    }

    @Provides
    fun providesRemoteDataSource(
        midFcstInfoService: MidFcstInfoService,
        vilageFcstInfoService: VilageFcstInfoService
    ): RemoteDataSource {
        return RemoteDataSource(
            midFcstInfoService = midFcstInfoService,
            vilageFcstInfoService = vilageFcstInfoService
        )
    }
}
